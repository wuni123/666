package com.rural.agri.controller;

import com.rural.agri.common.PageResult;
import com.rural.agri.common.Result;
import com.rural.agri.dto.NoticeDTO;
import com.rural.agri.entity.Notice;
import com.rural.agri.service.NoticeService;
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
 * 后台通知管理（需登录，JWT 拦截器鉴权）。
 */
@RestController
@RequestMapping("/api/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeService noticeService;

    @GetMapping("/page")
    public Result<PageResult<Notice>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(noticeService.page(page, pageSize));
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody NoticeDTO dto, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        noticeService.add(dto, userId);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody NoticeDTO dto) {
        noticeService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        noticeService.remove(id);
        return Result.success();
    }
}
