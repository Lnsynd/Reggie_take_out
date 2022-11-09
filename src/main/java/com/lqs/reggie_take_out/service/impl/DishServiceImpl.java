package com.lqs.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lqs.reggie_take_out.entity.Dish;
import com.lqs.reggie_take_out.mapper.DishMapper;
import com.lqs.reggie_take_out.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{
}
