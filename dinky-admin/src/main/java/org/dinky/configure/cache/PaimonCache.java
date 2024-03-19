/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.configure.cache;

import org.dinky.data.constant.PaimonTableConstant;
import org.dinky.data.paimon.CacheData;
import org.dinky.shaded.paimon.data.BinaryString;
import org.dinky.utils.PaimonUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.cache.support.AbstractValueAdaptingCache;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;

public class PaimonCache extends AbstractValueAdaptingCache {
    private static final Class<CacheData> clazz = CacheData.class;
    private final String cacheName;
    private static final String TABLE_NAME = PaimonTableConstant.DINKY_CACHE;
    /**
     * TIMEOUT CACHE
     */
    private final cn.hutool.cache.Cache<Object, Object> cache = new TimedCache<>(1000 * 60);

    public PaimonCache(String cacheName) {
        super(true);
        this.cacheName = cacheName;
    }

    @Override
    public String getName() {
        return cacheName;
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
            List<CacheData> cacheData = PaimonUtil.batchReadTable(
                    TABLE_NAME,
                    clazz,
                    x -> Arrays.asList(
                            x.equal(x.indexOf("cache_name"), BinaryString.fromString(cacheName)),
                            x.equal(x.indexOf("key"), BinaryString.fromString(strKey))));
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
        CacheData cacheData = CacheData.builder()
                .cacheTime(DateUtil.format(DateUtil.date(), "yyyy-MM-dd HH:mm"))
                .cacheName(cacheName)
                .key(strKey)
                .data(serialize(value))
                .build();
        PaimonUtil.write(TABLE_NAME, Collections.singletonList(cacheData), clazz);
    }

    @Override
    public void evict(Object key) {
        String strKey = Convert.toStr(key);
        cache.remove(strKey);
        CacheData cacheData =
                CacheData.builder().cacheName(cacheName).key(strKey).data("").build();
        PaimonUtil.write(TABLE_NAME, Collections.singletonList(cacheData), clazz);
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
