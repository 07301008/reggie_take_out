package com.itlxl.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itlxl.reggie.entity.OrderDetail;
import com.itlxl.reggie.mapper.OrderDetailMapper;
import com.itlxl.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
