package com.dlink.alert;

/**
 * AlertSendResponse
 *
 * @author wenmo
 * @since 2022/2/23 20:23
 **/
public class AlertSendResponse {
    private Integer errcode;
    private String errmsg;

    public AlertSendResponse() {
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
