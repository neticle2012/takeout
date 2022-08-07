package org.neticle.takeout.controller;

import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.Category;
import org.neticle.takeout.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
