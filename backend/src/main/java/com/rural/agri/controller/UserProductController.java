package com.rural.agri.controller;

import com.rural.agri.common.PageResult;
import com.rural.agri.common.Result;
import com.rural.agri.dto.ProductDTO;
import com.rural.agri.entity.Product;
import com.rural.agri.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 农户/普通用户发布接口（需登录，提交后进入待审核状态）。
 */
@RestController
@RequestMapping("/api/user/product")
@RequiredArgsConstructor
public class UserProductController {

    private final ProductService productService;

    @PostMapping
    public Result<Void> add(@Valid @RequestBody ProductDTO dto, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        productService.addByUser(dto, userId);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<PageResult<Product>> my(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        return Result.success(productService.pageMy(userId, page, pageSize));
    }
}
