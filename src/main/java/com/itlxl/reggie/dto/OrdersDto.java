package com.itlxl.reggie.dto;

import com.itlxl.reggie.entity.OrderDetail;
import com.itlxl.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {
    private List<OrderDetail> orderDetails;
}
