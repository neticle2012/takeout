package org.neticle.takeout.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.neticle.takeout.pojo.OrderDetail;
import org.neticle.takeout.pojo.Orders;

import java.util.List;

@Data
@ApiModel("订单Dto")
public class OrdersDto extends Orders {

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("收货人")
    private String consignee;

    @ApiModelProperty("订单对应的订单明细集合")
    private List<OrderDetail> orderDetails;
	
}
