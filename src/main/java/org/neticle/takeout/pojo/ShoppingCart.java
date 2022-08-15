package org.neticle.takeout.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车
 */
@Data
@ApiModel("购物车")
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    private Long id;

    //名称
    @ApiModelProperty("名称")
    private String name;

    //用户id
    @ApiModelProperty("用户id")
    private Long userId;

    //菜品id
    @ApiModelProperty("菜品id")
    private Long dishId;

    //套餐id
    @ApiModelProperty("套餐id")
    private Long setmealId;

    //口味
    @ApiModelProperty("口味")
    private String dishFlavor;

    //数量
    @ApiModelProperty("数量")
    private Integer number;

    //单份的金额
    @ApiModelProperty("单份的金额")
    private BigDecimal amount;

    //图片
    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
