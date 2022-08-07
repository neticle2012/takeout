package org.neticle.takeout.service.impl;

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
}
