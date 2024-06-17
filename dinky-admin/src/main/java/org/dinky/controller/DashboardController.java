package org.dinky.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dinky.data.dto.DashboardDTO;
import org.dinky.data.model.Dashboard;
import org.dinky.data.result.Result;
import org.dinky.service.DashboardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "ClusterInstance Instance Controller")
@RequestMapping("/api/Dashboard")
@SaCheckLogin
@AllArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @RequestMapping(value = "/saveOrUpdate", method = {RequestMethod.POST, RequestMethod.PUT})
    public Result<Void> saveOrUpdate(@RequestBody @Validated DashboardDTO dashboard) {
        dashboardService.saveOrUpdate(BeanUtil.toBean(dashboard, Dashboard.class));
        return Result.succeed();
    }

    @GetMapping("/getDashboardList")
    public Result<List<Dashboard>> getDashboardList() {
        return Result.succeed(dashboardService.list());
    }
    @GetMapping("/getDashboardById")
    public Result<Dashboard> getDashboardById(Integer id) {
        return Result.succeed(dashboardService.getById(id));
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(Integer id) {
        dashboardService.removeById(id);
        return Result.succeed();
    }
}
