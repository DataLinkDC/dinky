package com.dlink.exception;

import cn.dev33.satoken.exception.NotLoginException;

import com.dlink.common.result.Result;
import com.dlink.model.CodeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

/**
 * WebExceptionHandler
 *
 * @author wenmo
 * @since 2022/2/2 22:22
 */
@ControllerAdvice
@ResponseBody
public class WebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ExceptionHandler
    public Result busException(BusException e) {
        return Result.failed(e.getMessage());
    }

    @ExceptionHandler
    public Result notLoginException(NotLoginException e) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        response.setStatus(CodeEnum.NOTLOGIN.getCode());
        return Result.notLogin("该用户未登录!");
    }

    @ExceptionHandler
    public Result unknownException(Exception e) {
        logger.error("ERROR:", e);
        return Result.failed(e.getMessage());
    }
}
