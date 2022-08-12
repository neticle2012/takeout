package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.BaseContext;
import org.neticle.takeout.common.CustomException;
import org.neticle.takeout.common.R;
import org.neticle.takeout.mapper.OrdersMapper;
import org.neticle.takeout.pojo.*;
import org.neticle.takeout.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
        List<ShoppingCart> shoppingCarts = shoppingCartService.list();
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
}
