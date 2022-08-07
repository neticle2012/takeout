package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.mapper.DishMapper;
import org.neticle.takeout.pojo.Dish;
import org.neticle.takeout.service.DishService;
import org.springframework.stereotype.Service;

/**
 * @author Faruku123
 * @version 1.0
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
        implements DishService {
}
