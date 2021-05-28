package com.dlink.controller;

import com.dlink.common.result.Result;
import com.dlink.model.Task;
import com.dlink.model.User;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${dlink.login.username}")
    private String username;
    @Value("${dlink.login.password}")
    private String password;
    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) throws Exception {
        if(username.equals(user.getUsername())&&password.equals(user.getPassword())) {
            return Result.succeed(username, "登录成功");
        }else{
            return Result.failed("验证失败");
        }
    }

    /**
     * 退出
     */
    @DeleteMapping("/outLogin")
    public Result outLogin() throws Exception {
        return Result.succeed("退出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result current() throws Exception {
        return Result.succeed(username, "获取成功");
    }
}
