package org.neticle.takeout.controller;

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
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.addShoppingCart(shoppingCart);
    }

    /**
     * 查看购物车
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> listShoppingCart() {
        return shoppingCartService.listShoppingCart();
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> cleanShoppingCart() {
        return shoppingCartService.cleanShoppingCart();
    }

    /**
     * 减少购物车
     */
    @PostMapping("/sub")
    public R<ShoppingCart> subShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.subShoppingCart(shoppingCart);
    }
}
