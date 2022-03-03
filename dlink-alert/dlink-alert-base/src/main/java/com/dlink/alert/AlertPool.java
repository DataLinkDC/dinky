package com.dlink.alert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AlertPool
 *
 * @author wenmo
 * @since 2022/2/23 19:16
 **/
public class AlertPool {

    private static volatile Map<String, Alert> alertMap = new ConcurrentHashMap<>();

    public static boolean exist(String key) {
        if (alertMap.containsKey(key)) {
            return true;
        }
        return false;
    }

    public static Integer push(String key, Alert alert) {
        alertMap.put(key, alert);
        return alertMap.size();
    }

    public static Integer remove(String key) {
        alertMap.remove(key);
        return alertMap.size();
    }

    public static Alert get(String key) {
        return alertMap.get(key);
    }
}
