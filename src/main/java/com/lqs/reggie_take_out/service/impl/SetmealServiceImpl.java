package com.lqs.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lqs.reggie_take_out.common.CustomException;
import com.lqs.reggie_take_out.dto.SetmealDto;
import com.lqs.reggie_take_out.entity.Setmeal;
import com.lqs.reggie_take_out.entity.SetmealDish;
import com.lqs.reggie_take_out.mapper.SetmealMapper;
import com.lqs.reggie_take_out.service.SetmealDishService;
import com.lqs.reggie_take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealService setmealService;

    @Autowired
    SetmealDishService setmealDishService;

    /**
     * 保存套餐和菜品的关联信息
     *
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        // 保存信息到setmeal_dish表
        // 得到套餐中的菜品列表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        // 给菜品列表中的每个菜 添加统一的套餐id
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除菜品和套餐的关联关系
     *
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);

        // 如果不能删除，抛出异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖，不能删除");
        }

        // 如果可以删除，删除套餐表中的数据
        this.removeByIds(ids);

        // 删除关联表中的信息
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        // 查询基本套餐信息
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        // 查询套餐中菜品信息
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Override
    public void updateWithDishes(SetmealDto setmealDto) {
        // 更新套餐表的基本信息
        this.updateById(setmealDto);

        // 删除旧的setmealDto信息
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(lambdaQueryWrapper);

        // 将添加的新setmeal信息存入setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        // 给套餐中每个菜品设置了 套餐id
        List<SetmealDish> setmealDishList = setmealDishes.stream().map((dish) -> {
            dish.setSetmealId(setmealDto.getId());
            return dish;
        }).collect(Collectors.toList());

        // 将设置后的菜品列表 存入
        setmealDishService.saveBatch(setmealDishList);



    }
}
