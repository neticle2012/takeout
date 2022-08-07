package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.R;
import org.neticle.takeout.mapper.CategoryMapper;
import org.neticle.takeout.pojo.Category;
import org.neticle.takeout.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * @author Faruku123
 * @version 1.0
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {
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
}
