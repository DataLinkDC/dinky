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

package com.dlink.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.dlink.context.TenantContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dlink.dto.UserDTO;
import com.dlink.model.Tenant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

/**
 * tenant interceptor
 */
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Object object = StpUtil.getSession().get("user");
        if(object==null){
            StpUtil.logout();
            return false;
        }
        UserDTO userDTO = (UserDTO)object;

        Map<String, String> map = Arrays.stream(request.getCookies())
                .filter(t -> "tenantId".equals(t.getName()))
                .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
        if(MapUtils.isEmpty(map)){
            StpUtil.logout();
            return false;
        }

        List<Tenant> tenants = userDTO.getTenantList().stream()
                .filter(t -> t.getId() == Integer.parseInt(map.get("tenantId")))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(tenants)){
            StpUtil.logout();
            return false;
        }

        TenantContextHolder.set(tenants.get(0).getId());

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
