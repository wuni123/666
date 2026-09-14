package com.rural.agri.controller;

import com.rural.agri.common.Result;
import com.rural.agri.entity.Notice;
import com.rural.agri.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台通知展示（无需登录）。
 */
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public Result<List<Notice>> list() {
        return Result.success(noticeService.listAll());
    }
}
