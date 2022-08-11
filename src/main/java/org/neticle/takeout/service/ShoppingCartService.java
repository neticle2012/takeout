package org.neticle.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.ShoppingCart;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Faruku123
 * @version 1.0
 */
public interface ShoppingCartService extends IService<ShoppingCart> {
    R<ShoppingCart> addShoppingCart(ShoppingCart shoppingCart);
    R<List<ShoppingCart>> listShoppingCart();
    R<String> cleanShoppingCart();
    R<ShoppingCart> subShoppingCart(ShoppingCart shoppingCart);
}
