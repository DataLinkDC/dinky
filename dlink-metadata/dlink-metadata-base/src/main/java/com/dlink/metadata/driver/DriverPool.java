package com.dlink.metadata.driver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DriverPool
 *
 * @author wenmo
 * @since 2022/2/17 15:29
 **/
public class DriverPool {

    private static volatile Map<String, Driver> driverMap = new ConcurrentHashMap<>();

    public static boolean exist(String key) {
        if (driverMap.containsKey(key)) {
            return true;
        }
        return false;
    }

    public static Integer push(String key, Driver gainer) {
        driverMap.put(key, gainer);
        return driverMap.size();
    }

    public static Integer remove(String key) {
        driverMap.remove(key);
        return driverMap.size();
    }

    public static Driver get(String key) {
        return driverMap.get(key);
    }

}
