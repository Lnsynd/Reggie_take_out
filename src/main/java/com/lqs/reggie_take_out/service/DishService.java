package com.lqs.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lqs.reggie_take_out.dto.DishDto;
import com.lqs.reggie_take_out.entity.Dish;
import org.springframework.stereotype.Service;


public interface DishService extends IService<Dish> {

    // 新增菜品，同时插入对应的口味数据(操作两张表dish,dish_flavor)
    void saveWithFlavor(DishDto dishDto);

    // 根据id查询菜品和口味信息
    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
