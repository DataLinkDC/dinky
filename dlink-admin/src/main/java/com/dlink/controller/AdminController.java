package com.dlink.controller;

import com.dlink.common.result.Result;
import com.dlink.model.Task;
import com.dlink.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AdminController
 *
 * @author wenmo
 * @since 2021/5/28 15:52
 **/
@Slf4j
@RestController
@RequestMapping("/user")
public class AdminController {

    @Value("${dlink.login.username}")
    private String username;
    @Value("${dlink.login.password}")
    private String password;
    /**
     * 获取指定ID的信息
     */
    @PostMapping("/login")
    public Result getOneById(@RequestBody User user) throws Exception {
        if(username.equals(user.getUsername())&&password.equals(user.getPassword())) {
            return Result.succeed(username, "登录成功");
        }else{
            return Result.failed("验证失败");
        }
    }
}
