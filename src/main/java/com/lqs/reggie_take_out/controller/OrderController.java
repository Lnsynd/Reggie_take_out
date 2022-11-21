package com.lqs.reggie_take_out.controller;


import com.lqs.reggie_take_out.common.R;
import com.lqs.reggie_take_out.entity.Orders;
import com.lqs.reggie_take_out.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {


    @Autowired
    OrderService orderService;


    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("提交成功");
    }
}
