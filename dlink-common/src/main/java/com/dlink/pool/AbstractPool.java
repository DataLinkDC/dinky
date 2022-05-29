package com.dlink.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AbstractPool
 *
 * @author wenmo
 * @since 2022/5/28 19:40
 */
public abstract class AbstractPool<T>{

    public abstract Map<String, T> getMap();

    public boolean exist(String key) {
        if (getMap().containsKey(key)) {
            return true;
        }
        return false;
    }

    public int push(String key, T entity) {
        getMap().put(key, entity);
        return getMap().size();
    }

    public int remove(String key) {
        getMap().remove(key);
        return getMap().size();
    }

    public T get(String key) {
        return getMap().get(key);
    }

    public abstract void refresh(T entity);
}
