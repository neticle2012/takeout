package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.BaseContext;
import org.neticle.takeout.common.R;
import org.neticle.takeout.mapper.ShoppingCartMapper;
import org.neticle.takeout.pojo.ShoppingCart;
import org.neticle.takeout.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Faruku123
 * @version 1.0
 */
@Slf4j
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {
    @Override
    public R<ShoppingCart> addShoppingCart(ShoppingCart shoppingCartAdd) {
        log.info("购物车数据: {}", shoppingCartAdd);
        //设置用户id，指定当前是哪个用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        shoppingCartAdd.setUserId(userId);

        //查询当前菜品/套餐是否已经在购物车中了
        //SELECT * FROM shopping_cart WHERE user_id = userId AND dish_id = shoppingCart.dishId
        //SELECT * FROM shopping_cart WHERE user_id = userId AND setmeal_id = shoppingCart.setmealId
        LambdaQueryWrapper<ShoppingCart> lqwShoppingCart = new LambdaQueryWrapper<>();
        lqwShoppingCart.eq(ShoppingCart::getUserId, userId);
        Long dishId = shoppingCartAdd.getDishId();
        Long setmealId = shoppingCartAdd.getSetmealId();
        if (dishId != null) {//说明当前添加的是菜品
            lqwShoppingCart.eq(ShoppingCart::getDishId, dishId);
        } else {//说明当前添加的是套餐
            lqwShoppingCart.eq(ShoppingCart::getSetmealId, setmealId);
        }
        ShoppingCart shoppingCartOrigin = this.getOne(lqwShoppingCart);

        //TODO 如果用户点了两份同样的菜品/套餐，但是口味不同
        //如果已经存在，就在原来的数量基础上 +1（因为该方法传入进来的数量必定为1）
        if (shoppingCartOrigin != null) {
            shoppingCartOrigin.setNumber(shoppingCartOrigin.getNumber() + 1);
            this.updateById(shoppingCartOrigin);
        } else { //如果不存在，则数量为 1
            shoppingCartAdd.setNumber(1);
            this.save(shoppingCartAdd);
            shoppingCartOrigin = shoppingCartAdd;
        }
        return R.success(shoppingCartOrigin);
    }

    @Override
    public R<List<ShoppingCart>> listShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        //SELECT * FROM shopping_cart WHERE user_id = userId
        LambdaQueryWrapper<ShoppingCart> lqwShoppingCart = new LambdaQueryWrapper<>();
        lqwShoppingCart.eq(ShoppingCart::getUserId, userId)
                       .orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = this.list(lqwShoppingCart);
        return R.success(shoppingCarts);
    }

    @Override
    public R<String> cleanShoppingCart() {
        //DELETE FROM shopping_cart WHERE user_id = userId
        LambdaQueryWrapper<ShoppingCart> lqwShoppingCart = new LambdaQueryWrapper<>();
        lqwShoppingCart.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        this.remove(lqwShoppingCart);
        return R.success("清空购物车成功");
    }
}
