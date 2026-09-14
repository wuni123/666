package com.rural.agri.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {

    @NotBlank(message = "产品名称不能为空")
    private String name;

    private Long categoryId;

    private String origin;

    private String description;

    private BigDecimal price;

    private String unit;

    private String coverUrl;

    private String imageUrls;

    private Double longitude;

    private Double latitude;

    private Integer stock;

    /** 0-下架 1-上架 */
    private Integer status;
}
