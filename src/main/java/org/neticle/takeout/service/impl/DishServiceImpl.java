package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.DishDto;
import org.neticle.takeout.mapper.DishMapper;
import org.neticle.takeout.pojo.Category;
import org.neticle.takeout.pojo.Dish;
import org.neticle.takeout.pojo.DishFlavor;
import org.neticle.takeout.service.CategoryService;
import org.neticle.takeout.service.DishFlavorService;
import org.neticle.takeout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    @Autowired @Lazy //通过生成代理的方式解决构造循环依赖
    private CategoryService categoryService;

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
            if (category != null){
                //将分类名称设置到DishDto对象的categoryName属性中
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());
        pageInfoDto.setRecords(dtoList);
        return R.success(pageInfoDto);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     */
    @Override
    public R<DishDto> getDishWithFlavor(Long id) {
        //查询菜品基本信息，从dish表chaxun
        Dish dish = this.getById(id);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        //SELECT * FROM dish_flavor WHERE dish_id = dish.id
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(lqw);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(flavors);
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
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
        return R.success("修改菜品成功");
    }
}
