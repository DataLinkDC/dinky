package com.dlink.controller;

import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.gateway.result.TestResult;
import com.dlink.model.ClusterConfiguration;
import com.dlink.model.User;
import com.dlink.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserController
 *
 * @author wenmo
 * @since 2021/11/28 13:43
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 新增或者更新
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody User user) {
        if (Asserts.isNull(user.getId())) {
            return userService.registerUser(user);
        } else {
            userService.modifyUser(user);
            return Result.succeed("修改成功");
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<User> listClusterConfigs(@RequestBody JsonNode para) {
        return userService.selectForProTable(para, true);
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    public Result deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if(checkAdmin(id)){
                    error.add(id);
                    continue;
                }
                if (!userService.removeUser(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error.toString() + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    private static boolean checkAdmin(Integer id) {
        return id == 0;
    }

    /**
     * 获取指定ID的信息
     */
    @PostMapping("/getOneById")
    public Result getOneById(@RequestBody User user) {
        user = userService.getById(user.getId());
        return Result.succeed(user,"获取成功");
    }
}
