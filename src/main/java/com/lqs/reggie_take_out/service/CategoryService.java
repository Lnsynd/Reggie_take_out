package com.lqs.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lqs.reggie_take_out.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
