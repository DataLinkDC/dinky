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

    public static Builder builder() {
        return new Builder();
    }

    public boolean equals(Object o) {
        if (!(o instanceof PhoenixJdbcLookupOptions)) {
            return false;
        } else {
            PhoenixJdbcLookupOptions options = (PhoenixJdbcLookupOptions) o;
            return Objects.equals(this.cacheMaxSize, options.cacheMaxSize)
                    && Objects.equals(this.cacheExpireMs, options.cacheExpireMs)
                    && Objects.equals(this.maxRetryTimes, options.maxRetryTimes);
        }
    }

    public static class Builder {
        private long cacheMaxSize = -1L;
        private long cacheExpireMs = -1L;
        private int maxRetryTimes = 3;

        public Builder() {}

        public Builder setCacheMaxSize(long cacheMaxSize) {
            this.cacheMaxSize = cacheMaxSize;
            return this;
        }

        public Builder setCacheExpireMs(long cacheExpireMs) {
            this.cacheExpireMs = cacheExpireMs;
            return this;
        }

        public Builder setMaxRetryTimes(int maxRetryTimes) {
            this.maxRetryTimes = maxRetryTimes;
            return this;
        }

        public PhoenixJdbcLookupOptions build() {
            return new PhoenixJdbcLookupOptions(this.cacheMaxSize, this.cacheExpireMs, this.maxRetryTimes);
        }
    }
}
