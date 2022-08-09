package org.neticle.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.SetmealDto;
import org.neticle.takeout.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Faruku123
 * @version 1.0
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     */
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){
        return setmealService.saveSetmealWithDish(setmealDto);
    }

    /**
     * 套餐分页查询
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> getPage(int page, int pageSize, String name){
        return setmealService.getPage(page, pageSize, name);
    }
}
