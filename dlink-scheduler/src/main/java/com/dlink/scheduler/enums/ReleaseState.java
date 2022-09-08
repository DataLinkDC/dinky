package com.dlink.scheduler.enums;


/**
 * process define release state
 */
public enum ReleaseState {

    /**
     * 0 offline
     * 1 online
     */
    OFFLINE(0, "offline"),
    ONLINE(1, "online");

    ReleaseState(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    private final int code;
    private final String descp;

    public static ReleaseState getEnum(int value) {
        for (ReleaseState e : ReleaseState.values()) {
            if (e.ordinal() == value) {
                return e;
            }
        }
        //For values out of enum scope
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }
}
