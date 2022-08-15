package org.neticle.takeout.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.ShoppingCart;
import org.neticle.takeout.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Faruku123
 * @version 1.0
 */
@RestController
@RequestMapping("/shoppingCart")
@Api(tags = "购物车相关接口")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     */
    @ApiOperation(value = "添加购物车接口")
    @ApiImplicitParam(name = "shoppingCart", value = "购物车", required = true)
    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.addShoppingCart(shoppingCart);
    }

    /**
     * 查看购物车
     */
    @ApiOperation(value = "查看购物车接口")
    @GetMapping("/list")
    public R<List<ShoppingCart>> listShoppingCart() {
        return shoppingCartService.listShoppingCart();
    }

    /**
     * 清空购物车
     */
    @ApiOperation(value = "清空购物车接口")
    @DeleteMapping("/clean")
    public R<String> cleanShoppingCart() {
        return shoppingCartService.cleanShoppingCart();
    }

    /**
     * 减少购物车
     */
    @ApiOperation(value = "减少购物车接口")
    @ApiImplicitParam(name = "shoppingCart", value = "购物车", required = true)
    @PostMapping("/sub")
    public R<ShoppingCart> subShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.subShoppingCart(shoppingCart);
    }
}
