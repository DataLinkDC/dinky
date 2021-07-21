package com.dlink.assertion;

import com.dlink.exception.RunTimeException;

import java.util.Collection;

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


    public static void checkNull(String key,String msg) {
        if (key == null||"".equals(key)) {
            throw new RunTimeException(msg);
        }
    }

    public static void checkNotNull(Object object,String msg) {
        if (isNull(object)) {
            throw new RunTimeException(msg);
        }
    }

    public static void checkNullString(String key,String msg) {
        if (isNull(key)||isEquals("",key)) {
            throw new RunTimeException(msg);
        }
    }

    public static void checkNullCollection(Collection collection,String msg) {
        if(isNullCollection(collection)){
            throw new RunTimeException(msg);
        }
    }
}
