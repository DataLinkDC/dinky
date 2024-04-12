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

package org.dinky.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.annotations.ApiOperation;
import org.dinky.data.dto.LoginDTO;
import org.dinky.data.dto.UserDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.AuthException;
import org.dinky.data.result.Result;
import org.dinky.service.UserService;
import org.dinky.sso.web.LogoutController;

import java.util.List;

import javax.annotation.PostConstruct;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.NoArgsConstructor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author 杨泽翰
 */
@RestController
@NoArgsConstructor
@RequestMapping("/api/sso")
@ConfigurationProperties(prefix = "pac4j")
public class SsoCpntroller {
    @Value("${sso.baseUrl:localhost:8000}")
    private String baseUrl;
    @Value("${sso.enabled:false}")
    private Boolean ssoEnabled;

    @Value("${sso.centralLogout.defaultUrl:#{null}}")
    private String defaultUrl;

    @Value("${sso.centralLogout.logoutUrlPattern:#{null}}")
    private String logoutUrlPattern;

    @Value("${pac4j.properties.principalNameAttribute:#{null}}")
    private String principalNameAttribute;

    @Autowired
    private Config config;

    @Autowired
    private JEEContext webContext;

    @Autowired
    private ProfileManager profileManager;

    private LogoutController logoutController;

    @Autowired
    private UserService userService;

    @PostConstruct
    protected void afterPropertiesSet() {
        logoutController = new LogoutController();
        logoutController.setDefaultUrl(defaultUrl);
        logoutController.setLogoutUrlPattern(logoutUrlPattern);
        logoutController.setLocalLogout(true);
        logoutController.setCentralLogout(true);
        logoutController.setConfig(config);
        logoutController.setDestroySession(true);
    }

    @GetMapping("/token")
    public Result<UserDTO> ssoToken() throws AuthException {
        if (!ssoEnabled) {
            return Result.failed(Status.SINGLE_LOGIN_DISABLED);
        }
        List<CommonProfile> all = profileManager.getAll(true);
        String username = all.get(0).getAttribute(principalNameAttribute).toString();
        if (username == null) {
            throw new AuthException(Status.NOT_MATCHED_PRINCIPAL_NAME_ATTRIBUTE);
        }
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setSsoLogin(true);
        return userService.loginUser(loginDTO);
    }

    @GetMapping("/login")
    public ModelAndView ssoLogin()   {
        RedirectView redirectView = new RedirectView("http://"+baseUrl+"/#/user/login?from=sso");
        return new ModelAndView(redirectView);
    }

    @GetMapping("/logout")
    public void ssoLogout() {
        logoutController.logout(webContext.getNativeRequest(), webContext.getNativeResponse());
    }

    @GetMapping("/ssoEnableStatus")
    @SaIgnore
    @ApiOperation("Get SSO enable status")
    public Result<Boolean> ssoStatus() {
        return Result.succeed(ssoEnabled);
    }
}
