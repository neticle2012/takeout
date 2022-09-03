package org.neticle.takeout.service.impl;

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
import org.neticle.takeout.dto.SetmealDto;
import org.neticle.takeout.mapper.SetmealMapper;
import org.neticle.takeout.pojo.*;
import org.neticle.takeout.service.CategoryService;
import org.neticle.takeout.service.DishService;
import org.neticle.takeout.service.SetmealDishService;
import org.neticle.takeout.service.SetmealService;
import org.neticle.takeout.utils.RedisCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Faruku123
 * @version 1.0
 */
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    @Lazy
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisCache redisCache;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     */
    @Override
    @Transactional
    public R<String> saveSetmealWithDish(SetmealDto setmealDto) {
        log.info("套餐信息: {}", setmealDto);
        //保存套餐的基本信息 -> setmeal表INSERT
        this.save(setmealDto);

        //保存套餐和菜品的关联信息 -> setmeal_dish表INSERT
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
        redisCache.deleteObject("setmeals_in_category" + setmealDto.getCategoryId());
        return R.success("新增套餐成功");
    }

    @Override
    public R<Page<SetmealDto>> getPage(int page, int pageSize, String name) {
        Page<Setmeal> setmealPageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPageInfo = new Page<>();
        //SELECT * FROM setmeal WHERE name LIKE %name% ORDER BY update_time DESC
        //LIMIT (page - 1)*pageSize ,pageSize
        LambdaQueryWrapper<Setmeal> setmealLqw = new LambdaQueryWrapper<>();
        setmealLqw.like(name != null, Setmeal::getName, name);
        setmealLqw.orderByDesc(Setmeal::getUpdateTime);
        this.page(setmealPageInfo, setmealLqw);
        //将setmealPageInfo对象的属性都拷贝到setmealDtoPageInfo对象中（忽略records属性）
        BeanUtils.copyProperties(setmealPageInfo, setmealDtoPageInfo, "records");
        List<SetmealDto> setmealDtoList = setmealPageInfo.getRecords().stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);//将Setmeal对象的属性都拷贝到SetmealDto对象中
            Long categoryId = item.getCategoryId();//套餐的分类id
            Category category = categoryService.getById(categoryId);//根据分类id查询分类名称
            if (category != null) {
                //将分类名称设置到SetmealDto对象的categoryName属性中
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPageInfo.setRecords(setmealDtoList);
        return R.success(setmealDtoPageInfo);
    }

    /**
     * 根据id查询套餐信息和对应的菜品信息，该方法只在修改套餐时被调用
     * 注意：修改套餐时，套餐可能从分类A修改到分类B，因此需要从Redis缓存中同时删除掉分类A和分类B
     */
    @Override
    public R<SetmealDto> getSetmealWithDish(Long id) {
        //查询套餐基本信息，从setmeal表查询
        Setmeal setmeal = this.getById(id);
        //查询与该套餐关联的菜品信息，从setmeal_dish表查询
        //SELECT * FROM setmeal_dish WHERE setmeal_id = id
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(id != null, SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(lqw);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        setmealDto.setSetmealDishes(setmealDishes);
        redisCache.deleteObject("setmeals_in_category" + setmealDto.getCategoryId());
        return R.success(setmealDto);
    }

    /**
     * 更新套餐信息，同时更新与其关联的菜品信息
     */
    @Override
    @Transactional
    public R<String> updateSetmealWithDish(SetmealDto setmealDto) {
        Long setmealId = setmealDto.getId();//获取套餐id，为SetmealDish对象的setmealId属性赋值

        this.updateById(setmealDto);//更新dish表基本信息

        //先清理当前套餐对应的菜品数据 -> setmeal_dish表的DELETE操作
        //DELETE FROM setmeal_dish WHERE setmeal_id = setmealDto.id
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(lqw);
        //然后添加当前提交过来的菜品数据 -> setmeal_dish表的INSERT操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
        redisCache.deleteObject("setmeals_in_category" + setmealDto.getCategoryId());
        return R.success("修改套餐成功");
    }

    @Override
    @Transactional
    public R<String> updateSetmealStatus(int status, List<Long> ids) {
        log.info("status: {}", status);
        log.info("ids: {}", ids);
        //UPDATE setmeal SET `status` = status WHERE id IN (ids)
        LambdaUpdateWrapper<Setmeal> luwSetmeal = new LambdaUpdateWrapper<>();
        luwSetmeal.in(ids != null, Setmeal::getId, ids);
        //注意：这里不能使用luwSetmeal.set(Setmeal::getStatus, status)，因为公共字段update_time等也需要更新
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        this.update(setmeal, luwSetmeal);

        //如果一个套餐被起售，那么其关联的所有菜品都必须起售
        if (status == 1) {
            //SELECT DISTINCT dish_id FROM setmeal_dish WHERE setmeal_id in (ids)
            QueryWrapper<SetmealDish> lqwSetmealDish = new QueryWrapper<>();
            lqwSetmealDish.select("DISTINCT dish_id")
                          .lambda()
                          .in(SetmealDish::getSetmealId, ids);
            List<SetmealDish> setmealDishes = setmealDishService.list(lqwSetmealDish);
            List<Long> dishIds = setmealDishes.stream()
                                              .map(SetmealDish::getDishId)
                                              .collect(Collectors.toList());
            //UPDATE dish SET `status` = status WHERE id IN (dishIds)
            //注意：如果该套餐没有关联的菜品，那么dishIds = 空集合
            //其实这里可以不用判断，因为前端已经确保了每个套餐至少关联一个菜品
            if (dishIds.size() > 0) {
                LambdaQueryWrapper<Dish> luwDish = new LambdaQueryWrapper<>();
                luwDish.in(Dish::getId, dishIds);
                //注意：这里不能使用luwDish.set(Dish::getStatus, status)，因为公共字段update_time等也需要更新
                Dish dish = new Dish();
                dish.setStatus(status);
                dishService.update(dish, luwDish);
            }
        }
        deleteSetmealsFromRedis(ids);
        return R.success("售卖状态修改成功");
    }

    /**
     * 批量删除套餐，同时删除其关联的菜品数据
     */
    @Override
    @Transactional
    public R<String> deleteSetmealWithDish(List<Long> ids) {
        //查询当前套餐集合中是否存在正在起售的套餐，如果存在，则批量删除失败
        //SELECT COUNT(*) FROM setmeal WHERE id IN (ids) AND status = 1
        LambdaQueryWrapper<Setmeal> lqwSetmeal = new LambdaQueryWrapper<>();
        lqwSetmeal.in(ids != null, Setmeal::getId, ids)
                  .eq(Setmeal::getStatus, 1);
        if (this.count(lqwSetmeal) > 0) {
            throw new CustomException("存在正在售卖的套餐，无法删除");
        }
        //如果可以删除，先删除setmeal表中的数据
        this.removeByIds(ids);
        //再删除setmeal_dish表中关联的菜品数据
        //DELETE FROM setmeal_dish WHERE setmeal_id IN (ids)
        LambdaQueryWrapper<SetmealDish> lqwSetmealDish = new LambdaQueryWrapper<>();
        lqwSetmealDish.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(lqwSetmealDish);
        deleteSetmealsFromRedis(ids);
        return R.success("套餐删除成功");
    }

    @Override
    public R<List<Setmeal>> listSetmeal(Setmeal setmeal) {
        //先从redis中获取缓存数据
        String key = "setmeals_in_category" + setmeal.getCategoryId();
        List<Setmeal> setmeals = redisCache.getCacheObject(key);
        //如果存在直接返回，无需查询数据库
        if (setmeals != null) {
            return R.success(setmeals);
        }
        //如果不存在，需要查询数据库，将查询到的套餐数据缓存到Redis
        LambdaQueryWrapper<Setmeal> lqwSetmeal = new LambdaQueryWrapper<>();
        //SELECT * FROM setmeal WHERE category_id = setmeal.categoryId AND status = 1
        // ORDER BY update_time DESC
        lqwSetmeal.eq(Setmeal::getCategoryId, setmeal.getCategoryId())
                  .eq(Setmeal::getStatus, 1) //要求套餐必须是起售的，禁售套餐不显示
                  .orderByDesc(Setmeal::getUpdateTime);
        setmeals = this.list(lqwSetmeal);
        redisCache.setCacheObject(key, setmeals, 60, TimeUnit.MINUTES);
        return R.success(setmeals);
    }

    @Override
    public R<List<DishDto>> listDishesInSetmeal(Long setmealId) {
        //SELECT * FROM setmeal_dish WHERE setmeal_id = setmealId
        LambdaQueryWrapper<SetmealDish> lqwSetmealDish = new LambdaQueryWrapper<>();
        lqwSetmealDish.eq(SetmealDish::getSetmealId, setmealId);
        List<SetmealDish> setmealDishes = setmealDishService.list(lqwSetmealDish);
        List<DishDto> dishDtos = setmealDishes.stream().map((item) -> {
            //SELECT * FROM dish WHERE id = item.dishId
            Dish dish = dishService.getById(item.getDishId());
            DishDto dishDto = new DishDto();
            //将Dish对象的属性都拷贝到DishDto对象中
            BeanUtils.copyProperties(dish, dishDto);
            //将菜品份数设置到DishDto对象的copies属性中
            dishDto.setCopies(item.getCopies());
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtos);
    }

    /**
     * 根据套餐id获取最新的套餐信息，将其封装到ShoppingCart对象中
     */
    @Override
    public void setLatestSetmealInfoToShoppingCart(Long setmealId, ShoppingCart shoppingCart) {
        Setmeal setmeal = this.getById(setmealId);
        if (setmeal == null || setmeal.getStatus() == 0) {
            throw new CustomException("当前订单中有套餐不存在或者存在停售套餐");
        }
        //如果可以执行到这里，说明该套餐还存在，则设置套餐的最新信息
        shoppingCart.setName(setmeal.getName());
        shoppingCart.setImage(setmeal.getImage());
        shoppingCart.setSetmealId(setmealId);
        shoppingCart.setAmount(BigDecimal.valueOf(setmeal.getPrice().doubleValue() / 100));
    }

    /**
     * 从Redis缓存中删除掉当前套餐（多个）对应的分类
     */
    private void deleteSetmealsFromRedis(List<Long> ids) {
        //SELECT DISTINCT category_id FROM setmeal WHERE id IN (ids)
        QueryWrapper<Setmeal> lqwSetmeal = new QueryWrapper<>();
        lqwSetmeal.select("DISTINCT category_id")
               .lambda()
               .in(Setmeal::getId, ids);
        List<Setmeal> setmeals = this.list(lqwSetmeal);
        redisCache.deleteObject(setmeals.stream()
                .map((setmeal) -> "setmeals_in_category" + setmeal.getCategoryId())
                .collect(Collectors.toList()));
    }
}
