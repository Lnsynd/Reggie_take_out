package com.lqs.reggie_take_out.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lqs.reggie_take_out.common.R;
import com.lqs.reggie_take_out.dto.DishDto;
import com.lqs.reggie_take_out.entity.Category;
import com.lqs.reggie_take_out.entity.Dish;
import com.lqs.reggie_take_out.entity.DishFlavor;
import com.lqs.reggie_take_out.service.CategoryService;
import com.lqs.reggie_take_out.service.DishFlavorService;
import com.lqs.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    DishService dishService;

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    CategoryService categoryService;


    /**
     * 展示信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询信息
        lambdaQueryWrapper.like(name != null, Dish::getName, name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        // 分页查询
        dishService.page(pageInfo, lambdaQueryWrapper);

        // pageInfo的数据拷贝到dishDtoPage中
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        // 给records中的数据设置categoryName
        List<DishDto> list = records.stream().map((item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        })).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }


    /**
     * 回显，得到菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询对应的菜品信息
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        // 查询启售状态的菜品
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByAsc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lambdaQueryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                dishDto.setCategoryName(category.getName());
            }

            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(flavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }


    /**
     * 改变菜品的售卖状态
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable Integer status,@RequestParam List<Long> ids){
        log.info("状态：{},ids：{}",status,ids);
        List<Dish> dishList = ids.stream().map((id) -> {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishList);
        return R.success("改变状态成功");
    }


    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.removeByIds(ids);
        return R.success("删除成功");
    }

}
