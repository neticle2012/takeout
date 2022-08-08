package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.CustomException;
import org.neticle.takeout.common.R;
import org.neticle.takeout.mapper.CategoryMapper;
import org.neticle.takeout.pojo.Category;
import org.neticle.takeout.pojo.Dish;
import org.neticle.takeout.pojo.Setmeal;
import org.neticle.takeout.service.CategoryService;
import org.neticle.takeout.service.DishService;
import org.neticle.takeout.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Faruku123
 * @version 1.0
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public R<String> saveCatory(Category category) {
        log.info("category:{}", category);
        this.save(category);
        return R.success("新增分类成功");
    }

    @Override
    public R<Page<Category>> getPage(int page, int pageSize) {
        //构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        //SELECT * FROM category ORDER BY sort LIMIT (page - 1)*pageSize ,pageSize
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort进行排序
        lqw.orderByAsc(Category::getSort);
        this.page(pageInfo, lqw);//执行查询
        return R.success(pageInfo);
    }

    @Override
    public R<String> deleteCategory(Long id) {
        log.info("删除分类，id = {}", id);
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        //构造条件构造器
        //SELECT COUNT(*) FROM dish WHERE category_id = id
        LambdaQueryWrapper<Dish> dishLqw = new LambdaQueryWrapper<>();
        dishLqw.eq(Dish::getCategoryId, id);
        if (dishService.count(dishLqw) > 0) {
            //已经关联菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        //构造条件构造器
        //SELECT COUNT(*) FROM setmeal WHERE category_id = id
        LambdaQueryWrapper<Setmeal> setmealLqw = new LambdaQueryWrapper<>();
        setmealLqw.eq(Setmeal::getCategoryId, id);
        if (setmealService.count(setmealLqw) > 0) {
            //已经关联套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //正常删除分类
        this.removeById(id);
        return R.success("分类信息删除成功");
    }

    @Override
    public R<String> updateCategory(Category category) {
        log.info("修改分类信息: {}", category);
        this.updateById(category);
        return R.success("修改分类信息成功");
    }

    @Override
    public R<List<Category>> listCategory(Category category) {
        //构造条件构造器
        //SELECT * FROM category WHERE type = category.type
        // ORDER BY sort ASC, update_time DESC
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(category.getType() != null, Category::getType, category.getType());
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = this.list(lqw);
        return R.success(list);
    }
}
