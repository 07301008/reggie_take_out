package com.itlxl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itlxl.reggie.dto.SetmealDto;
import com.itlxl.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    // 新增套餐
    void saveWithDish(SetmealDto setmealDto);

    // 删除套餐
    void removeWithDish(List<Long> ids);

    // 更改套餐状态
    void status(int status, List<Long> ids);

    // 根据id查询套餐信息
    SetmealDto getByIdWithDish(Long id);

    // 修改套餐信息
    void updateWithDish(SetmealDto setmealDto);
}
