package com.lqs.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lqs.reggie_take_out.entity.Orders;


public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
