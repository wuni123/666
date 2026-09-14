package com.rural.agri.service;

import com.rural.agri.common.PageResult;
import com.rural.agri.dto.NoticeDTO;
import com.rural.agri.entity.Notice;

import java.util.List;

public interface NoticeService {

    PageResult<Notice> page(Integer page, Integer pageSize);

    List<Notice> listAll();

    void add(NoticeDTO dto, Long userId);

    void update(Long id, NoticeDTO dto);

    void remove(Long id);
}
