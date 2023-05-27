package org.dinky.controller;

import org.dinky.common.result.Result;
import org.dinky.constant.BaseConstant;
import org.dinky.enums.Status;

import java.util.Map;

public abstract class BaseController {
    /**
     * return data list
     *
     * @param result result code
     * @return result code
     */
    public static Result returnData(Map<String, Object> result) {
        Status status = (Status) result.get(BaseConstant.STATUS);
        if (status == Status.SUCCESS) {
            String msg = Status.SUCCESS.getMsg();
            Object data = result.get(BaseConstant.DATA);
            return getSuccessResult(msg, data);
        } else {
            Integer code = status.getCode();
            String msg = (String) result.get(BaseConstant.MSG);
            return getErrorResult(code, msg);
        }
    }

    /**
     * error handle
     *
     * @param code result code
     * @param msg result message
     * @return error result code
     */
    public static Result getErrorResult(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    /**
     * get success result
     *
     * @param msg message
     * @param data object data
     * @return result code
     */
    private static Result getSuccessResult(String msg, Object data) {
        Result result = new Result();
        result.setCode(Status.SUCCESS.getCode());
        result.setMsg(msg);

        result.setData(data);
        return result;
    }

    /**
     * this method returns a successful result with the status
     *
     * @param status
     * @return result code
     */
    public static Result getResultFromStatus(Status status){
        Result result = new Result();
        result.setCode(status.getCode());
        result.setMsg(status.getMsg());
        return result;
    }
}
