package org.neticle.takeout.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
菜品口味
 */
@Data
@ApiModel("菜品口味")
public class DishFlavor implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    private Long id;

    //菜品id
    @ApiModelProperty("菜品id")
    private Long dishId;

    //口味名称
    @ApiModelProperty("口味名称")
    private String name;

    //口味数据list
    @ApiModelProperty("口味数据list")
    private String value;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @ApiModelProperty("修改人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    //是否删除
    @ApiModelProperty("是否删除")
    @TableLogic
    private Integer isDeleted;

}
