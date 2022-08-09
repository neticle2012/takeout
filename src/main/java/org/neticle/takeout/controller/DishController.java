package org.neticle.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.DishDto;
import org.neticle.takeout.pojo.Dish;
import org.neticle.takeout.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 根据id查询菜品信息和对应的口味信息
     */
    @GetMapping("/{id}")
    public R<DishDto> getDish(@PathVariable("id") Long id){
        return dishService.getDishWithFlavor(id);
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        return dishService.updateDishWithFlavor(dishDto);
    }

    /**
     * 起售/停售和批量起售/停售，批量修改菜品的状态信息
     */
    @PostMapping("/status/{status}")
    public R<String> updateDishStatus(@PathVariable("status") int status,
                                      @RequestParam("ids") List<Long> ids){
        return dishService.updateDishStatus(status, ids);
    }

    /**
     * 删除和批量删除菜品
     */
    @DeleteMapping
    public R<String> deleteDish(@RequestParam("ids") List<Long> ids){
        return dishService.deleteDishWithFlavor(ids);
    }

    /**
     * 根据条件查询对应的菜品数据
     */
    @GetMapping("/list")
    public R<List<Dish>> listDish(Dish dish){
        return dishService.listDish(dish);
    }
}
