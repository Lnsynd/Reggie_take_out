package com.lqs.reggie_take_out;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lqs.reggie_take_out.entity.Dish;
import com.lqs.reggie_take_out.entity.DishFlavor;
import com.lqs.reggie_take_out.service.DishFlavorService;
import com.lqs.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
class ReggieTakeOutApplicationTests {

    @Autowired
    DishService dishService;

    @Autowired
    DishFlavorService dishFlavorService;

    @Test
    void contextLoads() {
        List<Dish> list = dishService.list();
        List<Long> dishIdList = list.stream().map((dish) -> {
            Long id = dish.getId();
            return id;
        }).collect(Collectors.toList());

        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.notIn(DishFlavor::getDishId,dishIdList);
        dishFlavorService.remove(lambdaQueryWrapper);
    }

}
