package com.lqs.reggie_take_out.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lqs.reggie_take_out.common.BaseContext;
import com.lqs.reggie_take_out.common.R;
import com.lqs.reggie_take_out.entity.ShoppingCart;
import com.lqs.reggie_take_out.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 添加到购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> save(@RequestBody ShoppingCart shoppingCart) {
        // 设置用户Id
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 查询该菜品或套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());

        if (dishId != null) {
            // 添加到购物车的是 菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            // 添加到购物车的是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(lambdaQueryWrapper);
        if (shoppingCartServiceOne != null) {
            // 如果存在，就数量+1
            Integer number = shoppingCartServiceOne.getNumber();
            shoppingCartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartServiceOne);
        } else {
            // 不存在 ，添加到购物车
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCartServiceOne = shoppingCart;
        }
        return R.success(shoppingCartServiceOne);
    }


    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("参数:{}", shoppingCart);
        // 查询用户Id
        Long currentId = BaseContext.getCurrentId();

        Long dishId = shoppingCart.getDishId();

        // 条件查询
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);

        // 如果传的参数是 菜品
        if (dishId != null) {
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            // 传来的参数是 套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(lambdaQueryWrapper);
        Integer number = shoppingCartServiceOne.getNumber();
        shoppingCartServiceOne.setNumber(number - 1);
        shoppingCartService.updateById(shoppingCartServiceOne);

        return R.success(shoppingCartServiceOne);
    }
}
