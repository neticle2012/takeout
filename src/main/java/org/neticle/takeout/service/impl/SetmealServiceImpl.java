package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.SetmealDto;
import org.neticle.takeout.mapper.SetmealMapper;
import org.neticle.takeout.pojo.Category;
import org.neticle.takeout.pojo.Setmeal;
import org.neticle.takeout.pojo.SetmealDish;
import org.neticle.takeout.service.CategoryService;
import org.neticle.takeout.service.SetmealDishService;
import org.neticle.takeout.service.SetmealService;
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
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal>
        implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired @Lazy
    private CategoryService categoryService;

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
        return R.success("修改套餐成功");
    }
}
