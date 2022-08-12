package org.neticle.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.neticle.takeout.common.R;
import org.neticle.takeout.dto.OrdersDto;
import org.neticle.takeout.pojo.Orders;

/**
 * @author Faruku123
 * @version 1.0
 */
public interface OrdersService extends IService<Orders> {
    R<String> submitOrders(Orders orders);
    R<Page<OrdersDto>> getUserPage(int page, int pageSize);
}
