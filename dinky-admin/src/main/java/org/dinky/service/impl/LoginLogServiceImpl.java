/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.service.impl;

import org.dinky.context.UserInfoContextHolder;
import org.dinky.data.dto.UserDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.model.LoginLog;
import org.dinky.data.model.User;
import org.dinky.mapper.LoginLogMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.LoginLogService;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.net.Ipv4Util;

@Service
public class LoginLogServiceImpl extends SuperServiceImpl<LoginLogMapper, LoginLog>
        implements LoginLogService {

    /**
     * insert login log record
     *
     * @param user
     * @param status
     * @param ip
     * @param msg
     */
    @Override
    public void saveLoginLog(User user, Integer status, String ip, String msg) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(user.getId());
        loginLog.setUsername(user.getUsername());
        loginLog.setLoginType(user.getUserType());
        loginLog.setAccessTime(LocalDateTime.now());
        loginLog.setIp(ip);
        loginLog.setStatus(status);
        loginLog.setMsg(msg);
        saveOrUpdate(loginLog);
    }

    /**
     * @param user
     * @param status
     * @param ip
     */
    @Override
    public void saveLoginLog(User user, Status status, String ip) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(user.getId());
        loginLog.setUsername(user.getUsername());
        loginLog.setLoginType(user.getUserType());
        loginLog.setAccessTime(LocalDateTime.now());
        loginLog.setIp(ip);
        loginLog.setStatus(status.getCode());
        loginLog.setMsg(status.getMsg());
        saveOrUpdate(loginLog);
    }

    /**
     * @param user
     * @param status
     */
    @Override
    public void saveLoginLog(User user, Status status) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(user.getId());
        loginLog.setUsername(user.getUsername());
        loginLog.setLoginType(user.getUserType());
        loginLog.setAccessTime(LocalDateTime.now());
        loginLog.setIp(Ipv4Util.LOCAL_IP);
        loginLog.setStatus(status.getCode());
        loginLog.setMsg(status.getMsg());
        saveOrUpdate(loginLog);
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public List<LoginLog> loginRecord(Integer userId) {
        UserDTO userDTO = UserInfoContextHolder.get(userId);
        List<LoginLog> loginLogList =
                getBaseMapper()
                        .selectList(
                                new LambdaQueryWrapper<LoginLog>()
                                        .eq(LoginLog::getUserId, userDTO.getUser().getId()));
        return loginLogList;
    }
}
