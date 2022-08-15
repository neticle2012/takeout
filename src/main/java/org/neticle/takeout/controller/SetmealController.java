package org.neticle.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     */
    @ApiOperation(value = "新增套餐接口")
    @ApiImplicitParam(name = "setmealDto", value = "套餐Dto", required = true)
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){
        return setmealService.saveSetmealWithDish(setmealDto);
    }

    /**
     * 套餐分页查询
     */
    @ApiOperation(value = "套餐分页查询接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", required = true),
                        @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
                        @ApiImplicitParam(name = "name", value = "套餐名称", required = false)})
    @GetMapping("/page")
    public R<Page<SetmealDto>> getPage(int page, int pageSize, String name){
        return setmealService.getPage(page, pageSize, name);
    }

    /**
     * 根据id查询套餐信息和对应的菜品信息，该方法只在修改套餐时被调用
     */
    @ApiOperation(value = "根据id查询套餐接口")
    @ApiImplicitParam(name = "id", value = "套餐主键id", required = true)
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmeal(@PathVariable("id") Long id){
        return setmealService.getSetmealWithDish(id);
    }

    /**
     * 修改套餐
     */
    @ApiOperation(value = "修改套餐接口")
    @ApiImplicitParam(name = "setmealDto", value = "套餐Dto", required = true)
    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto){
        return setmealService.updateSetmealWithDish(setmealDto);
    }

    /**
     * 起售/停售和批量起售/停售，批量修改套餐的状态信息
     */
    @ApiOperation(value = "套餐起售/停售和批量起售/停售接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "status", value = "要修改成的状态", required = true),
                        @ApiImplicitParam(name = "ids", value = "套餐主键id集合", required = true)})
    @PostMapping("/status/{status}")
    public R<String> updateSetmealStatus(@PathVariable("status") int status,
                                         @RequestParam("ids") List<Long> ids){
        return setmealService.updateSetmealStatus(status, ids);
    }

    /**
     * 删除和批量删除套餐
     */
    @ApiOperation(value = "删除和批量删除套餐接口")
    @ApiImplicitParam(name = "ids", value = "套餐主键id集合", required = true)
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam("ids") List<Long> ids){
        return setmealService.deleteSetmealWithDish(ids);
    }

    /**
     * 根据条件查询套餐数据
     */
    @ApiOperation(value = "根据条件查询套餐接口")
    @GetMapping("/list")
    public R<List<Setmeal>> listSetmeal(Setmeal setmeal){
        return setmealService.listSetmeal(setmeal);
    }

    /**
     * 查看指定id套餐下所有的菜品数据
     * 在移动端点击套餐图片时触发
     * 这里返回的是 DishDto对象，因为前端需要copies这个属性
     */
    @ApiOperation(value = "查看指定id套餐下所有菜品数据接口")
    @ApiImplicitParam(name = "id", value = "套餐id", required = true)
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> listDishesInSetmeal(@PathVariable("id") Long setmealId) {
        return setmealService.listDishesInSetmeal(setmealId);
    }
}
