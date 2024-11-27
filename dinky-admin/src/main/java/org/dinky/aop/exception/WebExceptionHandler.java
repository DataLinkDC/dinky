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

package org.dinky.aop.exception;

import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.result.Result;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * WebExceptionHandler
 *
 * @since 2022/2/2 22:22
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class WebExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    public Result<Void> busException(BusException e) {
        log.error(e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    private static final Map<String, Status> ERR_CODE_MAPPING = MapUtil.<String, Status>builder()
            .put(NotLoginException.NOT_TOKEN, Status.NOT_TOKEN)
            .put(NotLoginException.INVALID_TOKEN, Status.INVALID_TOKEN)
            .put(NotLoginException.TOKEN_TIMEOUT, Status.EXPIRED_TOKEN)
            .put(NotLoginException.BE_REPLACED, Status.BE_REPLACED)
            .put(NotLoginException.KICK_OUT, Status.KICK_OUT)
            .put(NotLoginException.TOKEN_FREEZE, Status.TOKEN_FREEZED)
            .put(NotLoginException.NO_PREFIX, Status.NO_PREFIX)
            .build();

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> notLoginException(NotLoginException notLoginException) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        if (response != null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }

        String type = notLoginException.getType();
        Status status = ERR_CODE_MAPPING.getOrDefault(type, Status.NOT_TOKEN);
        return Result.authorizeFailed(status);
    }

    /**
     * Parameter exception handler sets status code to 400
     *
     * @param e e
     * @return {@link Result}<{@link String}>
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public Result<String> paramExceptionHandler(MethodArgumentNotValidException e) {
        BindingResult exceptions = e.getBindingResult();
        // Check if there is any error message in the exception. If there is, use the message in the exception.
        // Otherwise, use the default message
        if (exceptions.hasErrors()) {
            List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                // All incorrect parameters are listed here. According to normal logic, only the first error is needed
                FieldError fieldError = (FieldError) errors.get(0);
                if (StringUtils.isNotBlank(fieldError.getDefaultMessage())) {
                    return Result.paramsError(
                            Status.GLOBAL_PARAMS_CHECK_ERROR, fieldError.getField(), fieldError.getDefaultMessage());
                }
                return Result.paramsError(
                        Status.GLOBAL_PARAMS_CHECK_ERROR_VALUE, fieldError.getField(), fieldError.getRejectedValue());
            }
        }
        return Result.paramsError(Status.REQUEST_PARAMS_ERROR);
    }
}
