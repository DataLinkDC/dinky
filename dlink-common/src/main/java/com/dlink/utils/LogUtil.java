package com.dlink.utils;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/**
 * LogUtil
 *
 * @author wenmo
 * @since 2022/2/11 15:46
 **/
public class LogUtil {

    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    public static String getError(Exception e){
//        e.printStackTrace();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String error = sw.toString();
        logger.error(sw.toString());
        return error;
    }

    public static String getError(String msg,Exception e){
//        e.printStackTrace();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        LocalDateTime now = LocalDateTime.now();
        String error = now.toString() + ": " + msg + " \nError message:\n " + sw.toString();
        logger.error(error);
        return error;
    }
}
