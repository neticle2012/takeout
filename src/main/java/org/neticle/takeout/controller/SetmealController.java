package org.neticle.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.DishDto;
import org.neticle.takeout.dto.SetmealDto;
import org.neticle.takeout.pojo.Setmeal;
import org.neticle.takeout.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 根据id查询套餐信息和对应的菜品信息
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmeal(@PathVariable("id") Long id){
        return setmealService.getSetmealWithDish(id);
    }

    /**
     * 修改套餐
     */
    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto){
        return setmealService.updateSetmealWithDish(setmealDto);
    }

    /**
     * 起售/停售和批量起售/停售，批量修改套餐的状态信息
     */
    @PostMapping("/status/{status}")
    public R<String> updateSetmealStatus(@PathVariable("status") int status,
                                         @RequestParam("ids") List<Long> ids){
        return setmealService.updateSetmealStatus(status, ids);
    }

    /**
     * 删除和批量删除套餐
     */
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam("ids") List<Long> ids){
        return setmealService.deleteSetmealWithDish(ids);
    }

    /**
     * 根据条件查询套餐数据
     */
    @GetMapping("/list")
    public R<List<Setmeal>> listSetmeal(Setmeal setmeal){
        return setmealService.listSetmeal(setmeal);
    }

    /**
     * 查看指定id套餐下所有的菜品数据
     * 在移动端点击套餐图片时触发
     * 这里返回的是 DishDto对象，因为前端需要copies这个属性
     */
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> listDishesInSetmeal(@PathVariable("id") Long setmealId) {
        return setmealService.listDishesInSetmeal(setmealId);
    }
}
