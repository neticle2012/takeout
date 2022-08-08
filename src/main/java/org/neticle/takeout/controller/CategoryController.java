package org.neticle.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.Category;
import org.neticle.takeout.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 根据id删除分类，删除之前需要进行判断
     */
    @DeleteMapping
    public R<String> deleteCategory(Long id){
        return categoryService.deleteCategory(id);
    }

    /**
     * 根据id修改分类信息
     */
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category){
        return categoryService.updateCategory(category);
    }

    /**
     * 根据条件查询分类数据
     */
    @GetMapping("/list")
    //这里通过pojo获取请求参数，若浏览器传输的请求参数的参数名和实体类中的属性名一致，那么请求参数就会为此属性赋值
    public R<List<Category>> listCategory(Category category){
        return categoryService.listCategory(category);
    }
}
