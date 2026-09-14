package com.rural.agri.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rural.agri.entity.Product;
import com.rural.agri.vo.CategoryStatVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT IFNULL(c.name, '未分类') AS name, COUNT(p.id) AS value " +
            "FROM product p LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.deleted = 0 " +
            "GROUP BY p.category_id, c.name ORDER BY value DESC")
    List<CategoryStatVO> countByCategory();
}
