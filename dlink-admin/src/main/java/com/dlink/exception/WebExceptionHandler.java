package com.dlink.exception;

import com.dlink.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public Result unknownException(Exception e) {
        logger.error("ERROR:",e);
        return Result.failed("系统出现错误, 请联系平台管理员!");
    }
}
