package org.neticle.takeout.dto;

import lombok.Data;
import org.neticle.takeout.pojo.Dish;
import org.neticle.takeout.pojo.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    //菜品对应的口味数据集合
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
