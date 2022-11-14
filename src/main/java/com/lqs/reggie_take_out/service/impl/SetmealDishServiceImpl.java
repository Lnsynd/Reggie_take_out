package com.lqs.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lqs.reggie_take_out.dto.SetmealDto;
import com.lqs.reggie_take_out.entity.SetmealDish;
import com.lqs.reggie_take_out.mapper.SetmealDishMapper;
import com.lqs.reggie_take_out.service.SetmealDishService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {

}
