package org.neticle.takeout.controller;

import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.Orders;
import org.neticle.takeout.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Faruku123
 * @version 1.0
 */
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public R<String> submitOrders(@RequestBody Orders orders) {
        return ordersService.submitOrders(orders);
    }
}
