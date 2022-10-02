package com.itlxl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itlxl.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {
    // 用户下单
    void submit(Orders orders);

    // 再来一单
    void again(Orders orders);
}
