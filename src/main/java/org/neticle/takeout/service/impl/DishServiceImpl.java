package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.DishDto;
import org.neticle.takeout.mapper.DishMapper;
import org.neticle.takeout.pojo.Dish;
import org.neticle.takeout.pojo.DishFlavor;
import org.neticle.takeout.service.DishFlavorService;
import org.neticle.takeout.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
