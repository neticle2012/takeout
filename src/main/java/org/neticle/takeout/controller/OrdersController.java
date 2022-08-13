package org.neticle.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.OrdersDto;
import org.neticle.takeout.pojo.Orders;
import org.neticle.takeout.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    /**
     * 用户查看自己所有的订单
     */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> getUserPage(int page, int pageSize) {
        return ordersService.getUserPage(page, pageSize);
    }

    /**
     * 用户再来一单
     */
    @PostMapping("/again")
    public R<String> submitOrdersAgain(@RequestBody Orders orders) {
        return ordersService.submitOrdersAgain(orders);
    }

    /**
     * 后台查询订单明细
     */
    @GetMapping("/page")
    public R<Page<Orders>> getOrderDetailPage(int page, int pageSize, String number,
                                              String beginTime, String endTime) {
        return ordersService.getOrderDetailPage(page, pageSize, number, beginTime, endTime);
    }

    /**
     * 后台修改订单状态
     */
    @PutMapping
    public R<String> updateOrderStatus(@RequestBody Orders orders) {
        return ordersService.updateOrderStatus(orders);
    }
}
