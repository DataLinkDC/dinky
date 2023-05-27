package org.dinky.controller;

import org.dinky.common.result.Result;
import org.dinky.constant.BaseConstant;
import org.dinky.enums.Status;

import java.util.Map;

public class BaseController {
    /**
     * return data list
     *
     * @param result result code
     * @return result code
     */
    public Result returnDataList(Map<String, Object> result) {
        Status status = (Status) result.get(BaseConstant.STATUS);
        if (status == Status.SUCCESS) {
            String msg = Status.SUCCESS.getMsg();
            Object datalist = result.get(BaseConstant.DATA_LIST);
            return success(msg, datalist);
        } else {
            Integer code = status.getCode();
            String msg = (String) result.get(BaseConstant.MSG);
            return error(code, msg);
        }
    }

    /**
     * success handle
     *
     * @param msg success message
     * @param list data list
     * @return success result code
     */
    public Result success(String msg, Object list) {
        return getResult(msg, list);
    }

    /**
     * error handle
     *
     * @param code result code
     * @param msg result message
     * @return error result code
     */
    public Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    /**
     * get result
     *
     * @param msg message
     * @param list object list
     * @return result code
     */
    private Result getResult(String msg, Object list) {
        Result result = new Result();
        result.setCode(Status.SUCCESS.getCode());
        result.setMsg(msg);

        result.setData(list);
        return result;
    }

    /**
     * This method returns a successful result with the status as a string
     * @param status
     * @return
     */
    public Result getResultFromStatus(Status status){
        Result result = new Result();
        result.setCode(status.getCode());
        result.setMsg(status.getMsg());
        return result;
    }
}
