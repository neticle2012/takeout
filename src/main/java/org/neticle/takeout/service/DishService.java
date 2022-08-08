package org.neticle.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.DishDto;
import org.neticle.takeout.pojo.Dish;

/**
 * @author Faruku123
 * @version 1.0
 */
public interface DishService extends IService<Dish> {
    R<String> saveDishWithFlavor(DishDto dishDto);
}
