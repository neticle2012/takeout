package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.BaseContext;
import org.neticle.takeout.common.CustomException;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.OrdersDto;
import org.neticle.takeout.mapper.OrdersMapper;
import org.neticle.takeout.pojo.*;
import org.neticle.takeout.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Faruku123
 * @version 1.0
 */
@Slf4j
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
        implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    @Transactional
    public R<String> submitOrders(Orders orders) {
        log.info("订单数据: {}", orders);
        //获取当前用户的id
        Long userId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据 -> shopping_cart表SELECT
        //SELECT * FROM shopping_cart WHERE user_id = userId
        LambdaQueryWrapper<ShoppingCart> lqwShoppingCart = new LambdaQueryWrapper<>();
        lqwShoppingCart.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(lqwShoppingCart);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }
        //查询用户数据 SELECT * FROM user WHERE id = userId
        User user = userService.getById(userId);
        //查询地址数据 SELECT * FROM address_book WHERE id = orders.addressBookId
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("用户地址信息有误，不能下单");
        }

        //准备订单对象Orders和订单明细对象OrderDetail集合
        long orderId = IdWorker.getId();//生成订单号
        AtomicInteger amount = new AtomicInteger(0);//总金额使用原子类
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            BigDecimal unitPrice = item.getAmount();//订单中每个菜品/套餐的单价
            Integer number = item.getNumber();//订单中每个菜品/套餐的数量
            orderDetail.setAmount(unitPrice);
            orderDetail.setNumber(number);
            amount.addAndGet(unitPrice.multiply(new BigDecimal(number)).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        orders.setNumber(orderId + "");
        orders.setStatus(2);//订单状态设置为待派送
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        //向订单表插入一条数据 -> order表INSERT
        this.save(orders);
        //向订单明细表插入多条数据 -> order_detail表INSERT
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据 -> shopping_cart表DELETE
        shoppingCartService.remove(lqwShoppingCart);
        return R.success("下单成功");
    }

    @Override
    public R<Page<OrdersDto>> getUserPage(int page, int pageSize) {
        Page<Orders> ordersPageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPageInfo = new Page<>(page, pageSize);
        //SELECT * FROM orders WHERE user_id = session.id ORDER BY order_time DESC
        // LIMIT (page - 1)*pageSize ,pageSize
        LambdaQueryWrapper<Orders> lqwOrders = new LambdaQueryWrapper<>();
        lqwOrders.eq(Orders::getUserId, BaseContext.getCurrentId())
                 .orderByDesc(Orders::getOrderTime);
        this.page(ordersPageInfo, lqwOrders);

        //将ordersPageInfo对象的属性都拷贝到ordersDtoPageInfo对象中（忽略records属性）
        BeanUtils.copyProperties(ordersPageInfo, ordersDtoPageInfo, "records");
        List<OrdersDto> ordersDtos = ordersPageInfo.getRecords().stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);//将Orders对象的属性都拷贝到OrdersDto对象中
            //SELECT * FROM order_detail WHERE order_id = item.number
            LambdaQueryWrapper<OrderDetail> lqwOrderDetail = new LambdaQueryWrapper<>();
            lqwOrderDetail.eq(OrderDetail::getOrderId, item.getNumber());
            List<OrderDetail> orderDetails = orderDetailService.list(lqwOrderDetail);
            if (orderDetails != null) {
                //将订单明细集合设置到OrdersDto对象的orderDetails属性中
                ordersDto.setOrderDetails(orderDetails);
            }
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPageInfo.setRecords(ordersDtos);
        return R.success(ordersDtoPageInfo);
    }

    @Override
    public R<String> submitOrdersAgain(Orders orders) {
        log.info("订单数据: {}", orders);
        String orderId = this.getById(orders.getId()).getNumber();//得到订单号
        log.info("订单号:{}", orderId);

        //清空当前用户的购物车，因为需要将要再来一单的菜品和套餐加入到购物车中
        shoppingCartService.cleanShoppingCart();
        //获取该订单对应的订单明细集合
        //SELECT * FROM order_detail WHERE order_id = orderId
        LambdaQueryWrapper<OrderDetail> lqwOrderDetail = new LambdaQueryWrapper<>();
        lqwOrderDetail.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderDetails = orderDetailService.list(lqwOrderDetail);
        //将订单明细集合中的菜品和套餐信息加入到购物车中
        //注意！必须从dish/setmeal表中获取每个菜品/套餐最新数据！！！
        //因为如果再来一单中的菜品/套餐出现变动（例如价格、图片），需要获取最新信息
        //如果再来一单中的菜品/套餐被禁售，或者口味已经不存在，则直接报错，无法再来一单
        List<ShoppingCart> shoppingCarts = orderDetails.stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();
            if (dishId != null) {//该条订单明细记录的是菜品 -> dish表
                dishService.setLatestDishInfoToShoppingCart(dishId, shoppingCart);
            } else {//该条订单明细记录的是套餐 -> setmeal表
                setmealService.setLatestSetmealInfoToShoppingCart(setmealId, shoppingCart);
            }
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartService.saveBatch(shoppingCarts);
        return R.success("操作成功");
    }

    @Override
    public R<Page<Orders>> getOrderDetailPage(int page, int pageSize, String number,
                                              String beginTime, String endTime) {
        Page<Orders> ordersPageInfo = new Page<>(page, pageSize);
        //SELECT * FROM orders WHERE number LIKE %number% AND order_time
        // BETWEEN beginTime AND endTime ORDER BY order_time DESC
        // LIMIT (page - 1)*pageSize ,pageSize
        LambdaQueryWrapper<Orders> lqwOrders = new LambdaQueryWrapper<>();
        lqwOrders.like(number != null, Orders::getNumber, number)
                 .between(StringUtils.isNotEmpty(beginTime) || StringUtils.isNotEmpty(endTime),
                         Orders::getOrderTime, beginTime, endTime)
                 .orderByDesc(Orders::getOrderTime);
        this.page(ordersPageInfo, lqwOrders);
        return R.success(ordersPageInfo);
    }
}
