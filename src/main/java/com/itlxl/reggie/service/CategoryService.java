package com.itlxl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itlxl.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
