package com.rural.agri.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rural.agri.common.BizException;
import com.rural.agri.common.PageResult;
import com.rural.agri.dto.ProductDTO;
import com.rural.agri.entity.Product;
import com.rural.agri.mapper.ProductMapper;
import com.rural.agri.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public PageResult<Product> page(String name, Long categoryId, Integer status, Integer auditStatus, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .like(StringUtils.hasText(name), Product::getName, name)
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .eq(status != null, Product::getStatus, status)
                .eq(auditStatus != null, Product::getAuditStatus, auditStatus)
                .orderByDesc(Product::getCreateTime);
        Page<Product> p = productMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageResult.of(p);
    }

    @Override
    public PageResult<Product> pageMy(Long userId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getCreateBy, userId)
                .orderByDesc(Product::getCreateTime);
        Page<Product> p = productMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageResult.of(p);
    }

    @Override
    public void add(ProductDTO dto, Long userId) {
        Product p = new Product();
        BeanUtils.copyProperties(dto, p);
        p.setCreateBy(userId);
        if (p.getStatus() == null) {
            p.setStatus(1);
        }
        p.setAuditStatus(1);
        productMapper.insert(p);
    }

    @Override
    public void addByUser(ProductDTO dto, Long userId) {
        Product p = new Product();
        BeanUtils.copyProperties(dto, p);
        p.setCreateBy(userId);
        if (p.getStatus() == null) {
            p.setStatus(0);
        }
        p.setAuditStatus(0);
        productMapper.insert(p);
    }

    @Override
    public void update(Long id, ProductDTO dto) {
        Product p = productMapper.selectById(id);
        if (p == null) {
            throw new BizException("产品不存在");
        }
        // auditStatus 不在 DTO 中，BeanUtils 不会覆盖，编辑时保持不变
        BeanUtils.copyProperties(dto, p);
        productMapper.updateById(p);
    }

    @Override
    public void review(Long id, Integer auditStatus) {
        Product p = productMapper.selectById(id);
        if (p == null) {
            throw new BizException("产品不存在");
        }
        p.setAuditStatus(auditStatus);
        if (auditStatus != null && auditStatus == 1) {
            p.setStatus(1);
        } else if (auditStatus != null && auditStatus == 2) {
            p.setStatus(0);
        }
        productMapper.updateById(p);
    }

    @Override
    public void remove(Long id) {
        productMapper.deleteById(id);
    }

    @Override
    public List<Product> listMapPoints() {
        return productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .eq(Product::getAuditStatus, 1)
                .isNotNull(Product::getLongitude)
                .isNotNull(Product::getLatitude));
    }
}
