package com.rural.agri.controller;

import com.rural.agri.common.PageResult;
import com.rural.agri.common.Result;
import com.rural.agri.dto.AuditDTO;
import com.rural.agri.dto.ProductDTO;
import com.rural.agri.entity.Product;
import com.rural.agri.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台农产品管理接口（需登录，JWT 拦截器鉴权）。
 */
@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @GetMapping("/page")
    public Result<PageResult<Product>> page(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(productService.page(name, categoryId, status, auditStatus, page, pageSize));
    }

    @PutMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @Valid @RequestBody AuditDTO dto) {
        productService.review(id, dto.getAuditStatus());
        return Result.success();
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody ProductDTO dto, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        productService.add(dto, userId);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        productService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        productService.remove(id);
        return Result.success();
    }
}
