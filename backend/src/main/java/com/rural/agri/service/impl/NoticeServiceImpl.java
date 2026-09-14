package com.rural.agri.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rural.agri.common.BizException;
import com.rural.agri.common.PageResult;
import com.rural.agri.dto.NoticeDTO;
import com.rural.agri.entity.Notice;
import com.rural.agri.mapper.NoticeMapper;
import com.rural.agri.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    public PageResult<Notice> page(Integer page, Integer pageSize) {
        Page<Notice> p = noticeMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getCreateTime));
        return PageResult.of(p);
    }

    @Override
    public List<Notice> listAll() {
        return noticeMapper.selectList(new LambdaQueryWrapper<Notice>()
                .orderByDesc(Notice::getCreateTime));
    }

    @Override
    public void add(NoticeDTO dto, Long userId) {
        Notice n = new Notice();
        BeanUtils.copyProperties(dto, n);
        n.setPublishBy(userId);
        noticeMapper.insert(n);
    }

    @Override
    public void update(Long id, NoticeDTO dto) {
        Notice n = noticeMapper.selectById(id);
        if (n == null) {
            throw new BizException("通知不存在");
        }
        BeanUtils.copyProperties(dto, n);
        noticeMapper.updateById(n);
    }

    @Override
    public void remove(Long id) {
        noticeMapper.deleteById(id);
    }
}
