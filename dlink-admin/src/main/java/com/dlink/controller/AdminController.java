package com.dlink.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.dlink.assertion.Asserts;
import com.dlink.common.result.Result;
import com.dlink.dto.LoginUTO;
import com.dlink.model.Task;
import com.dlink.model.User;
import com.dlink.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * AdminController
 *
 * @author wenmo
 * @since 2021/5/28 15:52
 **/
@Slf4j
@RestController
@RequestMapping("/api")
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginUTO loginUTO) {
        if(Asserts.isNull(loginUTO.isAutoLogin())){
            loginUTO.setAutoLogin(false);
        }
        return userService.loginUser(loginUTO.getUsername(), loginUTO.getPassword(),loginUTO.isAutoLogin());
    }

    /**
     * 退出
     */
    @DeleteMapping("/outLogin")
    public Result outLogin() {
        StpUtil.logout();
        return Result.succeed("退出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result current() throws Exception {
        try{
            return Result.succeed(StpUtil.getSession().get("user"), "获取成功");
        }catch (Exception e){
            return Result.failed("获取失败");
        }
    }
}
