package org.neticle.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.DishDto;
import org.neticle.takeout.pojo.Dish;

import java.util.List;

/**
 * @author Faruku123
 * @version 1.0
 */
public interface DishService extends IService<Dish> {
    R<String> saveDishWithFlavor(DishDto dishDto);
    R<Page<DishDto>> getPage(int page, int pageSize, String name);
    R<DishDto> getDishWithFlavor(Long id);
    R<String> updateDishWithFlavor(DishDto dishDto);
    R<String> updateDishStatus(int status, List<Long> ids);
    R<String> deleteDishWithFlavor(List<Long> ids);
}
