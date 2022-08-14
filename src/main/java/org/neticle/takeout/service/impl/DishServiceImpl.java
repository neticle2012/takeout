package org.neticle.takeout.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.BaseContext;
import org.neticle.takeout.common.CustomException;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.DishDto;
import org.neticle.takeout.mapper.DishMapper;
import org.neticle.takeout.pojo.*;
import org.neticle.takeout.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Faruku123
 * @version 1.0
 */
@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
        implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    @Lazy //通过生成代理的方式解决构造循环依赖
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    @Lazy
    private SetmealService setmealService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 新增菜品，同时插入菜品对应的口味数据
     */
    @Override
    @Transactional //两张表的插入要么同时成功，要么同时失败，需要使用事务控制
    public R<String> saveDishWithFlavor(DishDto dishDto) {
        log.info(dishDto.toString());
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

//        int i = 1 / 0;

        Long dishId = dishDto.getId();//获取菜品id，为DishFlavor对象的dishId属性赋值
        List<DishFlavor> flavors = dishDto.getFlavors();//菜品口味集合
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
        deleteFromRedis(dishDto);
        return R.success("新增菜品成功");
    }

    @Override
    public R<Page<DishDto>> getPage(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);//构造分页构造器对象
        Page<DishDto> pageInfoDto = new Page<>();
        //构造条件构造器
        //SELECT * FROM dish WHERE name LIKE %name% ORDER BY update_time DESC
        //LIMIT (page - 1)*pageSize ,pageSize
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null, Dish::getName, name);
        lqw.orderByDesc(Dish::getUpdateTime);
        this.page(pageInfo, lqw);
        //将pageInfo对象的属性都拷贝到pageInfoDto对象中（忽略records属性）
        BeanUtils.copyProperties(pageInfo, pageInfoDto, "records");
        List<DishDto> dtoList = pageInfo.getRecords().stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);//将Dish对象的属性都拷贝到DishDto对象中
            Long categoryId = item.getCategoryId();//菜品的分类id
            Category category = categoryService.getById(categoryId);//根据分类id查询分类名称
            if (category != null) {
                //将分类名称设置到DishDto对象的categoryName属性中
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());
        pageInfoDto.setRecords(dtoList);
        return R.success(pageInfoDto);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息，该方法只有在修改菜品时被调用
     */
    @Override
    public R<DishDto> getDishWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        //SELECT * FROM dish_flavor WHERE dish_id = dish.id
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(lqw);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(flavors);
        //注意：修改菜品时，菜品可能从分类A修改到分类B，因此需要从Redis缓存中同时删除掉分类A和分类B
        deleteFromRedis(dishDto);
        return R.success(dishDto);
    }

    /**
     * 更新菜品信息，同时更新口味信息
     */
    @Override
    @Transactional //两张表的更新要么同时成功，要么同时失败，需要使用事务控制
    public R<String> updateDishWithFlavor(DishDto dishDto) {
        Long dishId = dishDto.getId();//获取菜品id，为DishFlavor对象的dishId属性赋值

        //更新dish表基本信息
        this.updateById(dishDto);

        //先清理当前菜品对应的口味数据 -> dish_flavor表的DELETE操作
        //DELETE FROM dish_flavor WHERE dish_id = dishDto.id
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(lqw);
        //然后添加当前提交过来的口味数据 -> dish_flavor表的INSERT操作
        List<DishFlavor> flavors = dishDto.getFlavors();//菜品口味集合
        flavors = flavors.stream().map((item) -> {
            //这里菜品口味集合中是拿到了主键id的，如果先删除再插入会因为sql主键不能重复而报错，
            // 因此需要先清空主键id，让mybatisplus通过雪花算法重新生成
            item.setId(null);
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
        deleteFromRedis(dishDto);
        return R.success("修改菜品成功");
    }

    @Override
    @Transactional
    public R<String> updateDishStatus(int status, List<Long> ids) {
        log.info("status: {}", status);
        log.info("ids: {}", ids);
        //UPDATE dish SET `status` = status WHERE id IN (ids);
        LambdaUpdateWrapper<Dish> luw = new LambdaUpdateWrapper<>();
        luw.in(ids != null, Dish::getId, ids);
        //注意：这里不能使用luw.set(Dish::getStatus, status)，因为公共字段update_time等也需要更新
        Dish dish = new Dish();
        dish.setStatus(status);
        this.update(dish, luw);

        //如果一个菜品被停售，那么其关联的所有套餐都必须停售
        if (status == 0) {
            //SELECT DISTINCT setmeal_id FROM setmeal_dish WHERE dish_id in (ids)
            QueryWrapper<SetmealDish> lqwSetmealDish = new QueryWrapper<>();
            lqwSetmealDish.select("DISTINCT setmeal_id")
                          .lambda()
                          .in(SetmealDish::getDishId, ids);
            List<SetmealDish> setmealDishes = setmealDishService.list(lqwSetmealDish);
            List<Long> setmealIds = setmealDishes.stream()
                                                 .map(SetmealDish::getSetmealId)
                                                 .collect(Collectors.toList());
            //UPDATE setmeal SET `status` = status WHERE id IN (setmealIds)
            //注意：如果该菜品没有关联的套餐，那么setmealIds = 空集合
            if (setmealIds.size() > 0) {
                LambdaUpdateWrapper<Setmeal> luwSetmeal = new LambdaUpdateWrapper<>();
                luwSetmeal.in(Setmeal::getId, setmealIds);
                //注意：这里不能使用luwSetmeal.set(Setmeal::getStatus, status)，因为公共字段update_time等也需要更新
                Setmeal setmeal = new Setmeal();
                setmeal.setStatus(status);
                setmealService.update(setmeal, luwSetmeal);
            }
        }
        deleteFromRedis(ids);
        return R.success("售卖状态修改成功");
    }

    /**
     * 批量删除菜品，同时删除菜品对应的口味
     * 如果某个菜品正在起售，则整个批量删除失败
     * 此外，还需要进行菜品是否在套餐中的判断
     */
    @Override
    @Transactional
    public R<String> deleteDishWithFlavor(List<Long> ids) {
        log.info("ids:{}", ids);
        //查询当前菜品集合中是否存在正在起售的菜品，如果存在，则批量删除失败
        //SELECT COUNT(*) FROM dish WHERE id IN (ids) AND status = 1
        LambdaQueryWrapper<Dish> lqwDish = new LambdaQueryWrapper<>();
        lqwDish.in(ids != null, Dish::getId, ids)
               .eq(Dish::getStatus, 1);
        if (this.count(lqwDish) > 0){
            throw new CustomException("存在正在售卖的菜品，无法删除");
        }

        //查询当前菜品集合中是否存在与套餐关联的菜品，如果存在，则批量删除失败
        //SELECT COUNT(*) FROM setmeal_dish WHERE dish_id IN (ids)
        LambdaQueryWrapper<SetmealDish> lqwSetmealDish = new LambdaQueryWrapper<>();
        lqwSetmealDish.in(ids != null, SetmealDish::getDishId, ids);
        if (setmealDishService.count(lqwSetmealDish) > 0) {
            throw new CustomException("存在套餐中的菜品，无法删除");
        }

        //DELETE FROM dish WHERE id IN (ids)
        this.removeByIds(ids);
        //既然已经成功删除了菜品，那么对应的口味直接删除即可
        //DELETE FROM dish_flavor WHERE dish_id IN (ids)
        LambdaQueryWrapper<DishFlavor> lqwFlavor = new LambdaQueryWrapper<>();
        lqwFlavor.in(ids != null, DishFlavor::getDishId, ids);
        dishFlavorService.remove(lqwFlavor);
        deleteFromRedis(ids);
        return R.success("菜品删除成功");
    }

    @Override
    public R<List<DishDto>> listDish(Dish dish) {
        //先从redis中获取缓存数据
        String key = "dishes_in_category" + dish.getCategoryId();
        List<DishDto> dishDtos = JSON.parseArray(redisTemplate.opsForValue().get(key), DishDto.class);
        //如果存在直接返回，无需查询数据库
        if (dishDtos != null) {
            return R.success(dishDtos);
        }
        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis
        //SELECT * FROM dish WHERE category_id = dish.categoryId AND status = 1
        // ORDER BY sort ASC, update_time DESC
        LambdaQueryWrapper<Dish> lqwDish = new LambdaQueryWrapper<>();
        lqwDish.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        lqwDish.eq(Dish::getStatus, 1);//要求菜品必须是起售的，禁售菜品不显示
        lqwDish.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = this.list(lqwDish);
        dishDtos = dishes.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);//将Dish对象的属性都拷贝到DishDto对象中
            //根据菜品id查询对应的口味集合
            //SELECT * FROM dish_flavor WHERE dish_id = item.id
            LambdaQueryWrapper<DishFlavor> lqwDishFlavor = new LambdaQueryWrapper<>();
            lqwDishFlavor.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> dishFlavors = dishFlavorService.list(lqwDishFlavor);
            //将口味集合设置到DishDto对象的flavors属性中，如果菜品没有口味，那么dishFlavors集合为空集合
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key, JSON.toJSONString(dishDtos), 60, TimeUnit.MINUTES);
        return R.success(dishDtos);
    }

    /**
     * 根据菜品id获取最新的菜品信息，将其封装到ShoppingCart对象中
     */
    @Override
    public void setLatestDishInfoToShoppingCart(Long dishId, ShoppingCart shoppingCart) {
        Dish dish = this.getById(dishId);
        if (dish == null || dish.getStatus() == 0) {
            throw new CustomException("当前订单中有菜品不存在或者存在停售菜品");
        }
        String flavorValOld = shoppingCart.getDishFlavor();
        log.info("之前的口味值: {}", flavorValOld);
        //查询当前菜品对应的最新口味信息，从dish_flavor表查询
        //SELECT * FROM dish_flavor WHERE dish_id = dishId
        LambdaQueryWrapper<DishFlavor> lqwDishFlavor = new LambdaQueryWrapper<>();
        lqwDishFlavor.eq(DishFlavor::getDishId, dishId);
        List<DishFlavor> flavors = dishFlavorService.list(lqwDishFlavor);
        if (flavorValOld == null && flavors.size() > 0) {
            //说明原来没有口味的菜品添加了口味数据
            throw new CustomException("当前订单中有菜品增加了口味，请手动重新选择");
        }
        if (flavorValOld != null) {//该菜品存在口味信息
            Set<String> flavorsSet = new HashSet<>();
            for (DishFlavor flavor : flavors) {
                String flavorValNew = flavor.getValue();
                log.info("最新口味值: {}", flavorValNew);
                //["不要葱","不要蒜","不要香菜","不要辣"] -> 不要葱 不要蒜 不要香菜 不要辣
                flavorsSet.addAll(Arrays.stream(flavorValNew.substring(1, flavorValNew.length() - 1)
                                                            .split(","))
                                        .map(item -> item.substring(1, item.length() - 1))
                                        .collect(Collectors.toList()));
            }
            if (!flavorsSet.containsAll(Arrays.asList(flavorValOld.split(",")))) {
                throw new CustomException("当前订单中有菜品口味不存在");
            }
        }
        //如果可以执行到这里，说明该菜品和其口味都还存在，则设置菜品的最新信息
        shoppingCart.setName(dish.getName());
        shoppingCart.setImage(dish.getImage());
        shoppingCart.setDishId(dishId);
        shoppingCart.setAmount(BigDecimal.valueOf(dish.getPrice().doubleValue() / 100));
    }

    /**
     * 从Redis缓存中删除掉当前菜品对应的分类
     */
    private void deleteFromRedis(DishDto dishDto) {
        redisTemplate.delete("dishes_in_category" + dishDto.getCategoryId());
    }

    /**
     * 从Redis缓存中删除掉当前菜品（多个）对应的分类
     */
    private void deleteFromRedis(List<Long> ids) {
        //SELECT DISTINCT category_id FROM dish WHERE id IN (ids)
        QueryWrapper<Dish> lqwDish = new QueryWrapper<>();
        lqwDish.select("DISTINCT category_id")
               .lambda()
               .in(Dish::getId, ids);
        List<Dish> dishes = this.list(lqwDish);
        for (Dish dish : dishes) {
            redisTemplate.delete("dishes_in_category" + dish.getCategoryId());
        }
    }
}
