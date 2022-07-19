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
import org.apache.flink.annotation.PublicEvolving;
import org.apache.flink.util.Preconditions;


public class PhoenixJdbcExecutionOptions implements Serializable {
    public static final int DEFAULT_MAX_RETRY_TIMES = 3;
    private static final int DEFAULT_INTERVAL_MILLIS = 0;
    public static final int DEFAULT_SIZE = 5000;
    private final long batchIntervalMs;
    private final int batchSize;
    private final int maxRetries;

    private PhoenixJdbcExecutionOptions(long batchIntervalMs, int batchSize, int maxRetries) {
        Preconditions.checkArgument(maxRetries >= 0);
        this.batchIntervalMs = batchIntervalMs;
        this.batchSize = batchSize;
        this.maxRetries = maxRetries;
    }

    public long getBatchIntervalMs() {
        return this.batchIntervalMs;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public int getMaxRetries() {
        return this.maxRetries;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            PhoenixJdbcExecutionOptions that = (PhoenixJdbcExecutionOptions)o;
            return this.batchIntervalMs == that.batchIntervalMs && this.batchSize == that.batchSize && this.maxRetries == that.maxRetries;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.batchIntervalMs, this.batchSize, this.maxRetries});
    }

    public static PhoenixJdbcExecutionOptions.Builder builder() {
        return new PhoenixJdbcExecutionOptions.Builder();
    }

    public static PhoenixJdbcExecutionOptions defaults() {
        return builder().build();
    }

    public static final class Builder {
        private long intervalMs = 0L;
        private int size = 5000;
        private int maxRetries = 3;

        public Builder() {
        }

        public PhoenixJdbcExecutionOptions.Builder withBatchSize(int size) {
            this.size = size;
            return this;
        }

        public PhoenixJdbcExecutionOptions.Builder withBatchIntervalMs(long intervalMs) {
            this.intervalMs = intervalMs;
            return this;
        }

        public PhoenixJdbcExecutionOptions.Builder withMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public PhoenixJdbcExecutionOptions build() {
            return new PhoenixJdbcExecutionOptions(this.intervalMs, this.size, this.maxRetries);
        }
    }
}
