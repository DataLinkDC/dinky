package com.dlink.gateway;

import com.dlink.assertion.Asserts;

/**
 * SubmitType
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public enum GatewayType {

    LOCAL("l","local"),STANDALONE("s","standalone"),YARN_SESSION("ys","yarn-session"),YARN_APPLICATION("ya","yarn-application"),YARN_PER_JOB("ypj","yarn-per-job");

    private String value;
    private String longValue;

    GatewayType(String value, String longValue){
        this.value = value;
        this.longValue = longValue;
    }

    public String getValue() {
        return value;
    }

    public String getLongValue() {
        return longValue;
    }

    public static GatewayType get(String value){
        for (GatewayType type : GatewayType.values()) {
            if(Asserts.isEquals(type.getValue(),value)||Asserts.isEquals(type.getLongValue(),value)){
                return type;
            }
        }
        return GatewayType.YARN_APPLICATION;
    }

    public boolean equalsValue(String type){
        if(Asserts.isEquals(value,type)||Asserts.isEquals(longValue,type)){
            return true;
        }
        return false;
    }
}
