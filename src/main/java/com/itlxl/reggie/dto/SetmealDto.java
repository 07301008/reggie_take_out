package com.itlxl.reggie.dto;

import com.itlxl.reggie.entity.Setmeal;
import com.itlxl.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
