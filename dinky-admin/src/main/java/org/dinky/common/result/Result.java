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

package org.dinky.common.result;

import org.dinky.enums.Status;
import org.dinky.model.CodeEnum;
import org.dinky.utils.MessageResolverUtils;

import java.io.Serializable;
import java.text.MessageFormat;

import cn.hutool.core.date.DateTime;
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
public class Result<T> implements Serializable {

    private T datas;
    private Integer code;
    private String msg;
    private String time;
    private boolean success;

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public void setData(T data) {
        this.datas = data;
    }

    public Result(Status status) {
        if (status != null) {
            this.code = status.getCode();
            this.msg = status.getMsg();
        }
    }

    public Result(Integer code, String msg, T datas) {
        this.code = code;
        this.msg = msg;
        this.datas = datas;
    }

    public static <T> Result<T> succeed(String msg) {
        return of(null, CodeEnum.SUCCESS.getCode(), msg);
    }

    public static <T> Result<T> succeed() {
        return of(
                null,
                CodeEnum.SUCCESS.getCode(),
                MessageResolverUtils.getMessage("operate.success"));
    }

    public static <T> Result<T> succeed(T model, String msg) {
        return of(model, CodeEnum.SUCCESS.getCode(), msg);
    }

    public static <T> Result<T> succeed(T model) {
        return of(model, CodeEnum.SUCCESS.getCode(), "");
    }

    public static <T> Result<T> data(T model) {
        return of(model, CodeEnum.SUCCESS.getCode(), "");
    }

    public static <T> Result<T> of(T datas, Integer code, String msg) {
        return new Result<>(datas, code, msg, new DateTime().toString(), code == 0);
    }

    public static <T> Result<T> failed(String msg) {
        return of(null, CodeEnum.ERROR.getCode(), msg);
    }

    public static <T> Result<T> notLogin(String msg) {
        return of(null, CodeEnum.NOTLOGIN.getCode(), msg);
    }

    public static <T> Result<T> failed(T model, String msg) {
        return of(model, CodeEnum.ERROR.getCode(), msg);
    }

    /**
     * Call this function if there is any error
     *
     * @param status status
     * @param args args
     * @return result
     */
    public static <T> Result<T> errorWithArgs(Status status, Object... args) {
        return new Result<>(status.getCode(), MessageFormat.format(status.getMsg(), args));
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
        result.setMsg(status.getMsg());
        return result;
    }
}
