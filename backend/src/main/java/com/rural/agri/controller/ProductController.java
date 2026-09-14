package com.rural.agri.controller;

import com.rural.agri.common.PageResult;
import com.rural.agri.common.Result;
import com.rural.agri.entity.Product;
import com.rural.agri.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台农产品浏览接口（无需登录）。
 */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/page")
    public Result<PageResult<Product>> page(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(productService.page(name, categoryId, 1, 1, page, pageSize));
    }

    @GetMapping("/map")
    public Result<List<Product>> mapPoints() {
        return Result.success(productService.listMapPoints());
    }
}
