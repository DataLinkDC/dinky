package com.dlink.result;

import java.util.HashMap;
import java.util.Map;

/**
 * ResultPool
 *
 * @author wenmo
 * @since 2021/7/1 22:20
 */
public class ResultPool {

    private static volatile Map<String,SelectResult> results = new HashMap<String,SelectResult>();

    public static boolean containsKey(String key){
        return results.containsKey(key);
    }

    public static void put(SelectResult result) {
        results.put(result.getJobId(),result);
    }

    public static SelectResult get(String key){
        if(results.containsKey(key)){
            return results.get(key);
        }else{
            return SelectResult.buildDestruction(key);
        }
    }

    public static boolean remove(String key){
        if(results.containsKey(key)){
            results.remove(key);
            return true;
        }
        return false;
    }

    public static void clear(){
        results.clear();
    }

}
