package com.rural.agri.vo;

import lombok.Data;

import java.util.List;

@Data
public class DashboardVO {

    private Long productTotal;

    private Long onSaleTotal;

    private Long categoryTotal;

    private List<CategoryStatVO> categoryStats;
}
