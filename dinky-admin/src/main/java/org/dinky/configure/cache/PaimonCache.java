package org.dinky.configure.cache;

import org.dinky.utils.PaimonUtil;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

public class PaimonCache implements Cache {
    private static final String NAME = "paimon-cache";
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        PaimonUtil.createOrGetMetricsTable()
        return null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {

    }

    @Override
    public void evict(Object key) {

    }

    @Override
    public void clear() {

    }
}
