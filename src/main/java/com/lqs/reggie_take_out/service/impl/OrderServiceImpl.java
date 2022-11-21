package com.lqs.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lqs.reggie_take_out.common.BaseContext;
import com.lqs.reggie_take_out.common.CustomException;
import com.lqs.reggie_take_out.entity.*;
import com.lqs.reggie_take_out.mapper.OrderMapper;
import com.lqs.reggie_take_out.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    UserService userService;

    @Autowired
    AddressBookService addressBookService;


    @Override
    public void submit(Orders orders) {

        // 获得当前用户Id
        Long currentId = BaseContext.getCurrentId();

        // 查询用户的购物车信息
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(lambdaQueryWrapper);


        if (shoppingCarts == null) {
            throw new CustomException("购物车为空！");
        }

        // 查询用户信息
        User user = userService.getById(currentId);

        // 查询地址信息()
        // 查询出该订单的地址簿id
        Long addressBookId = orders.getAddressBookId();
        // 通过Id查出该订单的地址
        AddressBook addressBook = addressBookService.getById(addressBookId);

        //向订单表插入数据
        // 随机生成一个订单号
        long orderId = IdWorker.getId();

        // 金额
        AtomicInteger amount = new AtomicInteger(0);


        // 设置订单的数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));


        // 设置订单详情信息
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        //向订单表插入数据，一条数据
        this.save(orders);

        // 向订单明细表插入数据
        orderDetailService.saveBatch(orderDetails);

        // 清空购物车数据
        shoppingCartService.remove(lambdaQueryWrapper);
    }
}
