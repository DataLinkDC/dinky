package org.dinky.controller;

import org.dinky.common.result.Result;
import org.dinky.constant.BaseConstant;
import org.dinky.enums.Status;

import java.util.Map;

public abstract class BaseController {
    /**
     * this method returns a successful result with the status
     *
     * @param status
     * @return result code
     */
    public static Result<Void> getResultFromStatus(Status status){
        Result<Void> result = new Result<Void>();
        result.setCode(status.getCode());
        result.setMsg(status.getMsg());
        return result;
    }
}
