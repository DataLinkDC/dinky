package org.dinky.configure.cache;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.paimon.data.BinaryString;
import org.dinky.data.constant.PaimonTableConstant;
import org.dinky.data.paimon.CacheData;
import org.dinky.utils.PaimonUtil;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class PaimonCache extends AbstractValueAdaptingCache {
    private static  final Class<CacheData> clazz = CacheData.class;

    public static final String NAME = "paimon-cache";
    private final String cacheName;
    private static final String TABLE_NAME = PaimonTableConstant.DINKY_CACHE;
    /**
     * TIMEOUT CACHE
     */
    private final cn.hutool.cache.Cache<Object, Object> cache = new TimedCache<>(1000 * 60 * 30);

    public PaimonCache(String cacheName) {
        super(true);
        this.cacheName = cacheName;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return (T) get(key).get();
    }

    @Override
    protected Object lookup(Object key) {
        String strKey = Convert.toStr(key);
        Object o = cache.get(strKey);
        if (o == null) {
            PaimonUtil.createOrGetTable(TABLE_NAME, clazz);
            List<CacheData> cacheData = PaimonUtil.batchReadTable(TABLE_NAME, clazz, x -> Arrays.asList(x.equal(0, BinaryString.fromString(cacheName))
                    , x.equal(1, BinaryString.fromString(strKey)))
            );
            if (cacheData.isEmpty()) {
                return null;
            }
            return deserialize(cacheData.get(0).getData());
        }
        return o;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T) get(key).get();
    }

    @Override
    public void put(Object key, Object value) {
        String strKey = Convert.toStr(key);
        cache.put(strKey, value);
        PaimonUtil.createOrGetTable(TABLE_NAME, clazz);
        CacheData cacheData = CacheData.builder().cacheName(cacheName).key(strKey).data(serialize(value)).build();
        PaimonUtil.write(TABLE_NAME, Collections.singletonList(cacheData), clazz);
    }

    @Override
    public void evict(Object key) {
        cache.remove(key);
        // todo delete from table
    }

    @Override
    public void clear() {
        cache.clear();
        PaimonUtil.dropTable(TABLE_NAME);
    }

    public String serialize(Object object) {
        return JSON.toJSONString(object, JSONWriter.Feature.WriteClassName);
    }

    public Object deserialize(String json) {
        return JSON.parseObject(json, Object.class, JSONReader.Feature.SupportAutoType);
    }
}
