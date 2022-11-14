package com.lqs.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lqs.reggie_take_out.dto.SetmealDto;
import com.lqs.reggie_take_out.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    /**
     *  保存菜品和套餐的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    SetmealDto getByIdWithDish(Long id);

    void updateWithDishes(SetmealDto setmealDto);
}
