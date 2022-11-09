package com.lqs.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lqs.reggie_take_out.common.CustomException;
import com.lqs.reggie_take_out.entity.Category;
import com.lqs.reggie_take_out.entity.Dish;
import com.lqs.reggie_take_out.entity.Setmeal;
import com.lqs.reggie_take_out.mapper.CategoryMapper;
import com.lqs.reggie_take_out.service.CategoryService;
import com.lqs.reggie_take_out.service.DishService;
import com.lqs.reggie_take_out.service.SetmealService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;


    /**
     * 根据id删除分类
     *
     * @param id
     */
    @Override
    public void remove(Long id) {

        // 查询该分类是否关联了菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        // 关联菜品时候抛出异常
        if (count1 > 0) {
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询该分类是否关联了套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        // 不存在关联时
        super.removeById(id);
    }
}
