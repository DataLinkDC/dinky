package com.dlink.assertion;

import com.dlink.exception.JobException;

/**
 * Asserts
 *
 * @author wenmo
 * @since 2021/7/5 21:57
 */
public class Asserts {

    public static boolean isNotNull(Object object){
        return object!=null;
    }

    public static boolean isNull(Object object){
        return object==null;
    }

    public static boolean isNullString(String str){
        return str==null||"".equals(str);
    }

    public static boolean isEquals(String str1,String str2){
        if(isNull(str1)&&isNull(str2)){
            return true;
        }else if(isNull(str1)||isNull(str2)){
            return false;
        }else{
            return str1.equals(str2);
        }
    }

    public static void checkNull(String key,String msg) {
        if (key == null||"".equals(key)) {
            throw new JobException(msg);
        }
    }

}
