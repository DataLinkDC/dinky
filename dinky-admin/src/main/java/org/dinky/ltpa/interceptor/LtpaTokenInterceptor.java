package org.dinky.ltpa.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.dinky.assertion.Asserts;
import org.dinky.context.TenantContextHolder;
import org.dinky.context.UserInfoContextHolder;
import org.dinky.data.dto.UserDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.AuthException;
import org.dinky.data.model.rbac.User;
import org.dinky.data.result.Result;
import org.dinky.ltpa.handler.LtpaTokenHandler;
import org.dinky.ltpa.token.LtpaToken;
import org.dinky.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: lwjhn
 * Date: 2024/7/8 15:46
 * Description:
 */
@Component
@Slf4j
public class LtpaTokenInterceptor implements HandlerInterceptor {
    @Resource
    private LtpaTokenHandler ltpaTokenHandler;
    @Resource
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if(StpUtil.isLogin()){
                return true;
            }
            LtpaToken token = ltpaTokenHandler.validateLtpaToken(request);
            if (token != null) {
                String[] info = token.getUser().split("\\W+");
                // 0: userId
                String user = info[0];
                Integer userId = Integer.valueOf(user);
                if(UserInfoContextHolder.get(userId)!=null) {
                    StpUtil.login(userId, true);
                } else {
                    User userInfo = userService.getById(user);
                    if (Asserts.isNull(userInfo)) {
                        throw new AuthException(Status.USER_NOT_EXIST, user);
                    }
                    Result<UserDTO> result = userService.loginUser(userInfo, true);
                    if(Status.LOGIN_SUCCESS.getCode() != result.getCode()){
                        throw new AuthException(Status.findStatusByCode(result.getCode()).orElse(Status.LOGIN_FAILURE), result.getMsg());
                    }
                }
                // 1: tenantId
                if(info.length > 1) {
                    int finalTenantId = Integer.parseInt(info[1]);
                    TenantContextHolder.set(finalTenantId);
                    Cookie cookie = new Cookie("tenantId", info[1]);
                    cookie.setPath("/");
                    cookie.setMaxAge(Integer.MAX_VALUE);
                    response.addCookie(cookie);
                }
            }
        } catch (Exception e){
            log.warn("Exception {}", e.getMessage());
        }
        return true;
    }
}
