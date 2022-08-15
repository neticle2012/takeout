package org.neticle.takeout.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.neticle.takeout.pojo.Setmeal;
import org.neticle.takeout.pojo.SetmealDish;

import java.util.List;

@Data
@ApiModel("套餐Dto")
public class SetmealDto extends Setmeal {

    @ApiModelProperty("套餐对应的菜品数据集合")
    private List<SetmealDish> setmealDishes;

    @ApiModelProperty("分类名称")
    private String categoryName;
}
