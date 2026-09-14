package com.rural.agri.service;

import com.rural.agri.entity.Category;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface CategoryService {

    @Cacheable(cacheNames = "category", key = "'all'")
    List<Category> listAll();
}
