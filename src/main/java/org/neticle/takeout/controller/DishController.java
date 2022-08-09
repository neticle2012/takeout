package org.neticle.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.DishDto;
import org.neticle.takeout.pojo.Dish;
import org.neticle.takeout.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Faruku123
 * @version 1.0
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto){
        return dishService.saveDishWithFlavor(dishDto);
    }

    /**
     * 菜品信息分页查询
     */
    @GetMapping("/page")
    public R<Page<DishDto>> getPage(int page, int pageSize, String name){
        return dishService.getPage(page, pageSize, name);
    }
}
