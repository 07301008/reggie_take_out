package com.itlxl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itlxl.reggie.common.R;
import com.itlxl.reggie.dto.DishDto;
import com.itlxl.reggie.entity.Dish;
import com.itlxl.reggie.entity.DishFlavor;
import com.itlxl.reggie.mapper.DishMapper;
import com.itlxl.reggie.service.DishFlavorService;
import com.itlxl.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        // 菜品id
        Long dishId = dishDto.getId();
        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((flavor) -> {
            flavor.setDishId(dishId);
            return flavor;
        }).collect(Collectors.toList());

        // 保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 创建DishDto对象
        DishDto dishDto = new DishDto();
        // 查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);
        // 将dish中的信息拷贝到dishDto中
        BeanUtils.copyProperties(dish,dishDto);
        // 查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
        // 将查出的口味信息赋值给dishDto
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }


    /**
     * 修菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新菜品的基本信息到菜品表dish
        this.updateById(dishDto);
        // 菜品id
        Long dishId = dishDto.getId();
        // 清理当前菜品对应的口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(queryWrapper);
        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((flavor) -> {
            flavor.setDishId(dishId);
            return flavor;
        }).collect(Collectors.toList());

        // 保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithFlavor(Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            // 删除菜品表中的基本信息
            this.removeById(ids[i]);
            // 删除口味表中的基本信息
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, ids[i]);
            dishFlavorService.remove(queryWrapper);
        }
    }

    /**
     * 菜品状态信息更改
     * @param status
     * @param ids
     */
    @Override
    public void status(Integer status, Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            // 根据id查询出当前菜品信息
            Dish dish = this.getById(ids[i]);
            // 更改当前菜品状态
            dish.setStatus(status);
            // 更新当前菜品信息
            this.updateById(dish);
        }
    }
}
