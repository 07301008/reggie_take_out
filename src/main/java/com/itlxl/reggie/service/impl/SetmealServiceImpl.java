package com.itlxl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itlxl.reggie.common.CustomException;
import com.itlxl.reggie.dto.SetmealDto;
import com.itlxl.reggie.entity.Setmeal;
import com.itlxl.reggie.entity.SetmealDish;
import com.itlxl.reggie.mapper.SetmealMapper;
import com.itlxl.reggie.service.SetmealDishService;
import com.itlxl.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息到套餐表setmeal
        this.save(setmealDto);
        // 套餐id
        Long setmealId = setmealDto.getId();
        // 菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
            return setmealDish;
        }).collect(Collectors.toList());

        // 保存菜品数据到菜品套餐表 setmeal_dish
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(setmealLambdaQueryWrapper);
        if (count > 0) {
            // 不可以删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        // 可以删除，删除套餐表中的数据
        this.removeByIds(ids);
        // 删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper);

    }

    /**
     * 更改套餐状态
     * @param status
     * @param ids
     */
    @Override
    public void status(int status, List<Long> ids) {
//        for (Long id : ids) {
//            // 根据id查出当天套餐对象
//            Setmeal setmeal = this.getById(id);
//            // 设置当前套餐的状态
//            setmeal.setStatus(status);
//            // 更新当前套餐信息
//            this.updateById(setmeal);
//        }
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId,ids);
        updateWrapper.set(Setmeal::getStatus,status);
        this.update(updateWrapper);
    }

    /**
     * 根据套餐查询菜品信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);
        // 根据套餐id，查询菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    /**
     * 修改套餐信息
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        // 删除原来的套餐菜品信息
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        // 保存新的套餐菜品数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(setmealDish -> {
           setmealDish.setSetmealId(setmealDto.getId());
           return setmealDish;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }
}
