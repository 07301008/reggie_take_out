package com.itlxl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itlxl.reggie.common.BaseContext;
import com.itlxl.reggie.common.CustomException;
import com.itlxl.reggie.entity.*;
import com.itlxl.reggie.mapper.OrdersMapper;
import com.itlxl.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * 用户下单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        // 获取当前用户id
        Long userId = BaseContext.getCurrentId();
        // 查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartLambdaQueryWrapper);

        if (shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空不能下单");
        }

        // 查询用户数据
        User user = userService.getById(userId);
        // 查询地址数据
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }
        // 封装订单表数据
        long orderId = IdWorker.getId();// 订单号
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map(shoppingCart -> {
            amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetail.setImage(shoppingCart.getImage());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);
        orders.setUserId(user.getId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserName(user.getName());
        orders.setPhone(user.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        // 向订单表插入数据，一条数据
        this.save(orders);
        // 向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
        // 清空购物车数据
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }

    /**
     * 再来一单
     * @param orders
     */
    @Override
    public void again(Orders orders) {
        // 创建一个Order对象
        Orders ordersInfo = new Orders();
        // 根据传入的id查询出订单数据
        Orders oldOrders = this.getById(orders.getId());
        // 根据订单数据查询出订单明细表
        String oldOrdersNumber = oldOrders.getNumber();
        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, oldOrdersNumber);
        List<OrderDetail> orderDetails = orderDetailService.list(orderDetailLambdaQueryWrapper);
        long orderId = IdWorker.getId();// 订单号
        // 封装新订单明细表数据
        orderDetails = orderDetails.stream().map(oldOrderDetail -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(oldOrderDetail,orderDetail,"id");
            orderDetail.setOrderId(orderId);
            return orderDetail;
        }).collect(Collectors.toList());
        // 封装新订单表数据
        BeanUtils.copyProperties(oldOrders,ordersInfo,"id");
        ordersInfo.setNumber(String.valueOf(orderId));
        ordersInfo.setStatus(2);
        ordersInfo.setOrderTime(LocalDateTime.now());
        ordersInfo.setCheckoutTime(LocalDateTime.now());
        // 向订单表插入数据，一条数据
        this.save(ordersInfo);
        // 向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
    }
}
