package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.neticle.takeout.mapper.OrderDetailMapper;
import org.neticle.takeout.pojo.OrderDetail;
import org.neticle.takeout.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author Faruku123
 * @version 1.0
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
        implements OrderDetailService {
}
