package com.dlink.app.assertion;

import java.util.Collection;
import java.util.Map;

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
        return isNull(str)||"".equals(str);
    }

    public static boolean isNotNullString(String str){
        return !isNullString(str);
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

    public static boolean isEqualsIgnoreCase(String str1,String str2){
        if(isNull(str1)&&isNull(str2)){
            return true;
        }else if(isNull(str1)||isNull(str2)){
            return false;
        }else{
            return str1.equalsIgnoreCase(str2);
        }
    }

    public static boolean isNullCollection(Collection collection) {
        if (isNull(collection)||collection.size()==0) {
            return true;
        }
        return false;
    }

    public static boolean isNotNullCollection(Collection collection) {
        return !isNullCollection(collection);
    }

    public static boolean isNullMap(Map map) {
        if (isNull(map)||map.size()==0) {
            return true;
        }
        return false;
    }

    public static boolean isNotNullMap(Map map) {
        return !isNullMap(map);
    }

    public static void checkNull(String key,String msg) {
        if (key == null||"".equals(key)) {
            throw new RuntimeException(msg);
        }
    }

    public static void checkNotNull(Object object,String msg) {
        if (isNull(object)) {
            throw new RuntimeException(msg);
        }
    }

    public static void checkNullString(String key,String msg) {
        if (isNull(key)||isEquals("",key)) {
            throw new RuntimeException(msg);
        }
    }

    public static void checkNullCollection(Collection collection,String msg) {
        if(isNullCollection(collection)){
            throw new RuntimeException(msg);
        }
    }

    public static void checkNullMap(Map map,String msg) {
        if(isNullMap(map)){
            throw new RuntimeException(msg);
        }
    }
}
