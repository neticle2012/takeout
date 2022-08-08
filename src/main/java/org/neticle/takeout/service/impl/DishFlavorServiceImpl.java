package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.neticle.takeout.mapper.DishFlavorMapper;
import org.neticle.takeout.pojo.DishFlavor;
import org.neticle.takeout.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author Faruku123
 * @version 1.0
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
}
