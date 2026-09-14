package com.rural.agri.controller;

import com.rural.agri.common.Result;
import com.rural.agri.service.DashboardService;
import com.rural.agri.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台数据看板（需登录，JWT 拦截器鉴权）。
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public Result<DashboardVO> stats() {
        return Result.success(dashboardService.stats());
    }
}
