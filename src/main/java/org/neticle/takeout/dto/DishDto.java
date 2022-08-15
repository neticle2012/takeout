package org.neticle.takeout.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.neticle.takeout.pojo.Dish;
import org.neticle.takeout.pojo.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("菜品Dto")
public class DishDto extends Dish {
    //菜品对应的口味数据集合
    @ApiModelProperty("菜品对应的口味数据集合")
    private List<DishFlavor> flavors = new ArrayList<>();

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("份数")
    private Integer copies;
}
