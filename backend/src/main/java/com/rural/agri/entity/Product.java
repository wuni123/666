package com.rural.agri.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long categoryId;

    /** 产地，例如：江西省赣州市安远县 */
    private String origin;

    private String description;

    private BigDecimal price;

    /** 单位：斤 / 袋 / 盒 */
    private String unit;

    private String coverUrl;

    /** 多图，逗号分隔 */
    private String imageUrls;

    private Double longitude;

    private Double latitude;

    private Integer stock;

    /** 0-下架 1-上架 */
    private Integer status;

    /** 审核状态：0待审核 1通过 2驳回 */
    private Integer auditStatus;

    private Long createBy;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
