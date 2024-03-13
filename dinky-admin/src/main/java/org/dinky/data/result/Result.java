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

package org.dinky.data.result;

import org.dinky.data.enums.CodeEnum;
import org.dinky.data.enums.Status;
import org.dinky.utils.LogUtil;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Objects;

import cn.hutool.core.date.DateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回对象
 *
 * @since 2021/5/3 19:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Result", description = "Return Result")
public class Result<T> implements Serializable {

    /**
     * result data
     */
    @ApiModelProperty(
            value = "Result Data",
            name = "data",
            dataType = "T",
            required = true,
            allowEmptyValue = true,
            example = "[]")
    private T data;

    @ApiModelProperty(
            value = "Result Code",
            name = "code",
            dataType = "Integer",
            required = true,
            example = "0",
            notes = "CodeEnum")
    private Integer code;

    @ApiModelProperty(
            value = "Result Message",
            name = "msg",
            dataType = "String",
            required = true,
            example = "success",
            notes = "success: success, fail: fail")
    private String msg;

    /**
     * result time
     */
    @ApiModelProperty(
            value = "Result Time",
            name = "time",
            dataType = "DateTime",
            required = true,
            example = "2021-05-03 19:56:00",
            notes = "yyyy-MM-dd HH:mm:ss")
    private String time;

    /**
     * result success
     */
    @ApiModelProperty(
            value = "Result is Success",
            name = "success",
            dataType = "Boolean",
            required = true,
            example = "true",
            notes = "true: success, false: fail")
    private boolean success;

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Result(Status status) {
        if (status != null) {
            this.code = status.getCode();
            this.msg = status.getMessage();
        }
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> succeed(String msg) {
        return of(null, CodeEnum.SUCCESS.getCode(), msg);
    }

    public static <T> Result<T> succeed(Status status) {
        return of(null, CodeEnum.SUCCESS.getCode(), status.getMessage());
    }

    public static <T> Result<T> succeed() {
        return of(null, CodeEnum.SUCCESS.getCode(), Status.OPERATE_SUCCESS);
    }

    public static <T> Result<T> succeed(T model) {
        return of(model, CodeEnum.SUCCESS.getCode(), Status.SUCCESS);
    }

    public static <T> Result<T> succeed(T model, String msg) {
        return of(model, CodeEnum.SUCCESS.getCode(), msg);
    }

    public static <T> Result<T> succeed(T model, Status status) {
        return of(model, CodeEnum.SUCCESS.getCode(), status.getMessage());
    }

    public static <T> Result<T> succeed(T model, Status status, Object... args) {
        return of(model, CodeEnum.SUCCESS.getCode(), MessageFormat.format(status.getMessage(), args));
    }

    public static <T> Result<T> succeed(Status status, Object... args) {
        return of(null, CodeEnum.SUCCESS.getCode(), MessageFormat.format(status.getMessage(), args));
    }

    public static <T> Result<T> data(T model) {
        return of(model, CodeEnum.SUCCESS.getCode(), "");
    }

    public static <T> Result<T> of(T data, Integer code, String msg) {
        return new Result<>(
                data, code, msg, new DateTime().toString(), Objects.equals(code, CodeEnum.SUCCESS.getCode()));
    }

    public static <T> Result<T> of(T data, Integer code, Status status) {
        return new Result<>(
                data,
                code,
                status.getMessage(),
                new DateTime().toString(),
                Objects.equals(code, CodeEnum.SUCCESS.getCode()));
    }

    public static <T> Result<T> failed() {
        return of(null, CodeEnum.ERROR.getCode(), Status.FAILED);
    }

    public static <T> Result<T> failed(String msg) {
        return of(null, CodeEnum.ERROR.getCode(), msg);
    }

    public static <T> Result<T> failed(Status status) {
        return of(null, CodeEnum.ERROR.getCode(), status.getMessage());
    }

    public static <T> Result<T> failed(Status status, Object... args) {
        return of(null, CodeEnum.ERROR.getCode(), MessageFormat.format(status.getMessage(), args));
    }

    public static <T> Result<T> failed(T model, Status status, Object... args) {
        return of(model, CodeEnum.ERROR.getCode(), MessageFormat.format(status.getMessage(), args));
    }

    public static <T> Result<T> failed(T model, String msg) {
        return of(model, CodeEnum.ERROR.getCode(), msg);
    }

    public static <T> Result<T> failed(T model, Status status) {
        return of(model, CodeEnum.ERROR.getCode(), status.getMessage());
    }

    public static <T> Result<T> authorizeFailed(Status status) {
        return of(null, CodeEnum.AUTHORIZE_ERROR.getCode(), status.getMessage());
    }

    public static <T> Result<T> authorizeFailed(Status status, Object... args) {
        return of(null, CodeEnum.AUTHORIZE_ERROR.getCode(), MessageFormat.format(status.getMessage(), args));
    }

    public static <T> Result<T> authorizeFailed(String msg) {
        return of(null, CodeEnum.AUTHORIZE_ERROR.getCode(), msg);
    }

    public static Result<String> exception(String msg, Exception e) {
        return of(LogUtil.getError(e), CodeEnum.EXCEPTION.getCode(), msg);
    }

    public static <T> Result<T> paramsError(Status status, Object... args) {
        return of(null, CodeEnum.PARAMS_ERROR.getCode(), MessageFormat.format(status.getMessage(), args));
    }

    /**
     * Call this function if there is any error
     *
     * @param status status
     * @param args   args
     * @return result
     */
    public static <T> Result<T> errorWithArgs(Status status, Object... args) {
        return new Result<>(status.getCode(), MessageFormat.format(status.getMessage(), args));
    }

    /**
     * this method returns a successful result with the status
     *
     * @param status
     * @return {@link Result}{@link Void} result status information
     */
    public static Result<Void> getResultFromStatus(Status status) {
        Result<Void> result = new Result<Void>();
        result.setCode(status.getCode());
        result.setMsg(status.getMessage());
        return result;
    }
}
