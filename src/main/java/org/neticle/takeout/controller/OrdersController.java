package org.neticle.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "订单相关接口")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     */
    @ApiOperation(value = "用户下单接口")
    @ApiImplicitParam(name = "orders", value = "订单", required = true)
    @PostMapping("/submit")
    public R<String> submitOrders(@RequestBody Orders orders) {
        return ordersService.submitOrders(orders);
    }

    /**
     * 用户查看自己所有的订单
     */
    @ApiOperation(value = "订单分页查询接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", required = true),
                        @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true)})
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> getUserPage(int page, int pageSize) {
        return ordersService.getUserPage(page, pageSize);
    }

    /**
     * 用户再来一单
     */
    @ApiOperation(value = "用户再来一单接口")
    @ApiImplicitParam(name = "orders", value = "订单", required = true)
    @PostMapping("/again")
    public R<String> submitOrdersAgain(@RequestBody Orders orders) {
        return ordersService.submitOrdersAgain(orders);
    }

    /**
     * 后台查询订单明细
     */
    @ApiOperation(value = "后台查询订单明细接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", required = true),
                        @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
                        @ApiImplicitParam(name = "number", value = "订单号", required = false),
                        @ApiImplicitParam(name = "beginTime", value = "开始时间", required = false),
                        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false)})
    @GetMapping("/page")
    public R<Page<Orders>> getOrderDetailPage(int page, int pageSize, String number,
                                              String beginTime, String endTime) {
        return ordersService.getOrderDetailPage(page, pageSize, number, beginTime, endTime);
    }

    /**
     * 后台修改订单状态
     */
    @ApiOperation(value = "后台修改订单状态接口")
    @ApiImplicitParam(name = "orders", value = "订单", required = true)
    @PutMapping
    public R<String> updateOrderStatus(@RequestBody Orders orders) {
        return ordersService.updateOrderStatus(orders);
    }
}
