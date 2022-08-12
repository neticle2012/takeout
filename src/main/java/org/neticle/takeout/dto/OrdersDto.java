package org.neticle.takeout.dto;

import lombok.Data;
import org.neticle.takeout.pojo.OrderDetail;
import org.neticle.takeout.pojo.Orders;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
