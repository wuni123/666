package com.rural.agri.service;

import com.rural.agri.common.PageResult;
import com.rural.agri.dto.ProductDTO;
import com.rural.agri.entity.Product;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface ProductService {

    /**
     * 分页查询产品。
     *
     * @param status      状态过滤：传 null 表示不过滤（后台管理用），传 1 只查上架（前台浏览用）
     * @param auditStatus 审核状态过滤：传 null 表示不过滤（后台管理用），传 1 只查已通过（前台浏览用）
     */
    PageResult<Product> page(String name, Long categoryId, Integer status, Integer auditStatus, Integer page, Integer pageSize);

    /** 查询用户自己发布的产品（我的发布）。 */
    PageResult<Product> pageMy(Long userId, Integer page, Integer pageSize);

    /** 管理员发布，默认上架且审核通过。 */
    @CacheEvict(cacheNames = "product:map", allEntries = true)
    void add(ProductDTO dto, Long userId);

    /** 农户/普通用户提交发布，进入待审核状态。 */
    @CacheEvict(cacheNames = "product:map", allEntries = true)
    void addByUser(ProductDTO dto, Long userId);

    @CacheEvict(cacheNames = "product:map", allEntries = true)
    void update(Long id, ProductDTO dto);

    @CacheEvict(cacheNames = "product:map", allEntries = true)
    void remove(Long id);

    /** 管理员审核：auditStatus=1 通过（自动上架），=2 驳回（保持/下架）。 */
    @CacheEvict(cacheNames = "product:map", allEntries = true)
    void review(Long id, Integer auditStatus);

    /** 查询所有已上架且带经纬度的产品，用于地图标注（缓存 30 分钟）。 */
    @Cacheable(cacheNames = "product:map", key = "'all'")
    List<Product> listMapPoints();
}
