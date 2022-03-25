package org.apache.flink.connector.phoenix.internal.options;

import java.io.Serializable;
import java.util.Objects;

public class PhoenixJdbcLookupOptions implements Serializable {
    private final long cacheMaxSize;
    private final long cacheExpireMs;
    private final int maxRetryTimes;

    public PhoenixJdbcLookupOptions(long cacheMaxSize, long cacheExpireMs, int maxRetryTimes) {
        this.cacheMaxSize = cacheMaxSize;
        this.cacheExpireMs = cacheExpireMs;
        this.maxRetryTimes = maxRetryTimes;
    }

    public long getCacheMaxSize() {
        return this.cacheMaxSize;
    }

    public long getCacheExpireMs() {
        return this.cacheExpireMs;
    }

    public int getMaxRetryTimes() {
        return this.maxRetryTimes;
    }

    public static PhoenixJdbcLookupOptions.Builder builder() {
        return new PhoenixJdbcLookupOptions.Builder();
    }

    public boolean equals(Object o) {
        if (!(o instanceof PhoenixJdbcLookupOptions)) {
            return false;
        } else {
            PhoenixJdbcLookupOptions options = (PhoenixJdbcLookupOptions)o;
            return Objects.equals(this.cacheMaxSize, options.cacheMaxSize) && Objects.equals(this.cacheExpireMs, options.cacheExpireMs) && Objects.equals(this.maxRetryTimes, options.maxRetryTimes);
        }
    }

    public static class Builder {
        private long cacheMaxSize = -1L;
        private long cacheExpireMs = -1L;
        private int maxRetryTimes = 3;

        public Builder() {
        }

        public PhoenixJdbcLookupOptions.Builder setCacheMaxSize(long cacheMaxSize) {
            this.cacheMaxSize = cacheMaxSize;
            return this;
        }

        public PhoenixJdbcLookupOptions.Builder setCacheExpireMs(long cacheExpireMs) {
            this.cacheExpireMs = cacheExpireMs;
            return this;
        }

        public PhoenixJdbcLookupOptions.Builder setMaxRetryTimes(int maxRetryTimes) {
            this.maxRetryTimes = maxRetryTimes;
            return this;
        }

        public PhoenixJdbcLookupOptions build() {
            return new PhoenixJdbcLookupOptions(this.cacheMaxSize, this.cacheExpireMs, this.maxRetryTimes);
        }
    }
}