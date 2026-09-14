package com.rural.agri.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rural.agri.entity.Product;
import com.rural.agri.mapper.CategoryMapper;
import com.rural.agri.mapper.ProductMapper;
import com.rural.agri.service.DashboardService;
import com.rural.agri.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public DashboardVO stats() {
        DashboardVO vo = new DashboardVO();
        vo.setProductTotal(productMapper.selectCount(null));
        vo.setOnSaleTotal(productMapper.selectCount(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)));
        vo.setCategoryTotal(categoryMapper.selectCount(null));
        vo.setCategoryStats(productMapper.countByCategory());
        return vo;
    }
}
