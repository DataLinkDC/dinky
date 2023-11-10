package org.dinky.configure.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.LinkedHashSet;

public class PaimonCacheManager extends AbstractCacheManager {

    @Override
    protected Collection<? extends Cache> loadCaches() {
        Collection<Cache> caches = new LinkedHashSet<>();
        for (String cacheName : this.getCacheNames()) {
            Cache cache = this.getCache(cacheName);
            caches.add(cache);
        }
        return caches;
    }

    @Override
    protected Cache getMissingCache(String name) {
        return new PaimonCache(name);
    }
}
