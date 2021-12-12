package com.dlink.utils;

/**
 * RunTimeUtil
 *
 * @author wenmo
 * @since 2021/12/11
 **/
public class RunTimeUtil {

    public static void recovery(Object obj){
        obj = null;
        System.gc();
    }
}
