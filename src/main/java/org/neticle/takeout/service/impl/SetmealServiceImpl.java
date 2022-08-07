package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.mapper.SetmealMapper;
import org.neticle.takeout.pojo.Setmeal;
import org.neticle.takeout.service.SetmealService;
import org.springframework.stereotype.Service;

/**
 * @author Faruku123
 * @version 1.0
 */
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal>
        implements SetmealService {
}
