package org.neticle.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.Category;
import org.neticle.takeout.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Faruku123
 * @version 1.0
 * 分类管理
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     */
    @PostMapping
    public R<String> saveCatory(@RequestBody Category category){
        return categoryService.saveCatory(category);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page<Category>> getPage(int page, int pageSize){
        return categoryService.getPage(page, pageSize);
    }
}
