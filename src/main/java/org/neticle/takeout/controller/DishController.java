package org.neticle.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     */
    @ApiOperation(value = "新增菜品接口")
    @ApiImplicitParam(name = "dishDto", value = "菜品Dto", required = true)
    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto){
        return dishService.saveDishWithFlavor(dishDto);
    }

    /**
     * 菜品信息分页查询
     */
    @ApiOperation(value = "菜品分页查询接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", required = true),
                        @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
                        @ApiImplicitParam(name = "name", value = "菜品名称", required = false)})
    @GetMapping("/page")
    public R<Page<DishDto>> getPage(int page, int pageSize, String name){
        return dishService.getPage(page, pageSize, name);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息，该方法只有在修改菜品时被调用
     */
    @ApiOperation(value = "根据id查询菜品接口")
    @ApiImplicitParam(name = "id", value = "菜品主键id", required = true)
    @GetMapping("/{id}")
    public R<DishDto> getDish(@PathVariable("id") Long id){
        return dishService.getDishWithFlavor(id);
    }

    /**
     * 修改菜品
     */
    @ApiOperation(value = "修改菜品接口")
    @ApiImplicitParam(name = "dishDto", value = "菜品Dto", required = true)
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        return dishService.updateDishWithFlavor(dishDto);
    }

    /**
     * 起售/停售和批量起售/停售，批量修改菜品的状态信息
     */
    @ApiOperation(value = "菜品起售/停售和批量起售/停售接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "status", value = "要修改成的状态", required = true),
                        @ApiImplicitParam(name = "ids", value = "菜品主键id集合", required = true)})
    @PostMapping("/status/{status}")
    public R<String> updateDishStatus(@PathVariable("status") int status,
                                      @RequestParam("ids") List<Long> ids){
        return dishService.updateDishStatus(status, ids);
    }

    /**
     * 删除和批量删除菜品
     */
    @ApiOperation(value = "删除和批量删除菜品接口")
    @ApiImplicitParam(name = "ids", value = "菜品主键id集合", required = true)
    @DeleteMapping
    public R<String> deleteDish(@RequestParam("ids") List<Long> ids){
        return dishService.deleteDishWithFlavor(ids);
    }

    /**
     * 根据条件查询对应的菜品数据
     */
    @ApiOperation(value = "根据条件查询菜品接口")
    @GetMapping("/list")
    public R<List<DishDto>> listDish(Dish dish){
        return dishService.listDish(dish);
    }
}
