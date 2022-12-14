package org.neticle.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.DishDto;
import org.neticle.takeout.dto.SetmealDto;
import org.neticle.takeout.pojo.Setmeal;
import org.neticle.takeout.pojo.ShoppingCart;

import java.util.List;

/**
 * @author Faruku123
 * @version 1.0
 */
public interface SetmealService extends IService<Setmeal> {
    R<String> saveSetmealWithDish(SetmealDto setmealDto);
    R<Page<SetmealDto>> getPage(int page, int pageSize, String name);
    R<SetmealDto> getSetmealWithDish(Long id);
    R<String> updateSetmealWithDish(SetmealDto setmealDto);
    R<String> updateSetmealStatus(int status, List<Long> ids);
    R<String> deleteSetmealWithDish(List<Long> ids);
    R<List<Setmeal>> listSetmeal(Setmeal setmeal);
    R<List<DishDto>> listDishesInSetmeal(Long setmealId);
    void setLatestSetmealInfoToShoppingCart(Long setmealId, ShoppingCart shoppingCart);
}
