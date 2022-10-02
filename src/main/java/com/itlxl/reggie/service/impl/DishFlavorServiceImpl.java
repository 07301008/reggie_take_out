package com.itlxl.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itlxl.reggie.entity.DishFlavor;
import com.itlxl.reggie.mapper.DishFlavorMapper;
import com.itlxl.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
