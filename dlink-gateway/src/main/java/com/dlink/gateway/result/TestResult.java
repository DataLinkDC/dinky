package com.dlink.gateway.result;

/**
 * TestResult
 *
 * @author wenmo
 * @since 2021/11/27 16:12
 **/
public class TestResult {
    private boolean isAvailable;
    private String error;

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getError() {
        return error;
    }

    public TestResult(boolean isAvailable, String error) {
        this.isAvailable = isAvailable;
        this.error = error;
    }

    public static TestResult success(){
        return new TestResult(true,null);
    }

    public static TestResult fail(String error){
        return new TestResult(false,error);
    }
}
