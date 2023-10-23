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
import java.util.Optional;

public class PhoenixJdbcReadOptions implements Serializable {

    private final String query;
    private final String partitionColumnName;
    private final Long partitionLowerBound;
    private final Long partitionUpperBound;
    private final Integer numPartitions;
    private final int fetchSize;
    private final boolean autoCommit;

    private PhoenixJdbcReadOptions(
            String query,
            String partitionColumnName,
            Long partitionLowerBound,
            Long partitionUpperBound,
            Integer numPartitions,
            int fetchSize,
            boolean autoCommit) {
        this.query = query;
        this.partitionColumnName = partitionColumnName;
        this.partitionLowerBound = partitionLowerBound;
        this.partitionUpperBound = partitionUpperBound;
        this.numPartitions = numPartitions;
        this.fetchSize = fetchSize;
        this.autoCommit = autoCommit;
    }

    public Optional<String> getQuery() {
        return Optional.ofNullable(this.query);
    }

    public Optional<String> getPartitionColumnName() {
        return Optional.ofNullable(this.partitionColumnName);
    }

    public Optional<Long> getPartitionLowerBound() {
        return Optional.ofNullable(this.partitionLowerBound);
    }

    public Optional<Long> getPartitionUpperBound() {
        return Optional.ofNullable(this.partitionUpperBound);
    }

    public Optional<Integer> getNumPartitions() {
        return Optional.ofNullable(this.numPartitions);
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

    public boolean getAutoCommit() {
        return this.autoCommit;
    }

    public static PhoenixJdbcReadOptions.Builder builder() {
        return new PhoenixJdbcReadOptions.Builder();
    }

    public boolean equals(Object o) {
        if (!(o instanceof JdbcReadOptions)) {
            return false;
        } else {
            PhoenixJdbcReadOptions options = (PhoenixJdbcReadOptions) o;
            return Objects.equals(this.query, options.query)
                    && Objects.equals(this.partitionColumnName, options.partitionColumnName)
                    && Objects.equals(this.partitionLowerBound, options.partitionLowerBound)
                    && Objects.equals(this.partitionUpperBound, options.partitionUpperBound)
                    && Objects.equals(this.numPartitions, options.numPartitions)
                    && Objects.equals(this.fetchSize, options.fetchSize)
                    && Objects.equals(this.autoCommit, options.autoCommit);
        }
    }

    public static class Builder {
        protected String query;
        protected String partitionColumnName;
        protected Long partitionLowerBound;
        protected Long partitionUpperBound;
        protected Integer numPartitions;
        protected int fetchSize = 0;
        protected boolean autoCommit = true;

        public Builder() {}

        public PhoenixJdbcReadOptions.Builder setQuery(String query) {
            this.query = query;
            return this;
        }

        public PhoenixJdbcReadOptions.Builder setPartitionColumnName(String partitionColumnName) {
            this.partitionColumnName = partitionColumnName;
            return this;
        }

        public PhoenixJdbcReadOptions.Builder setPartitionLowerBound(long partitionLowerBound) {
            this.partitionLowerBound = partitionLowerBound;
            return this;
        }

        public PhoenixJdbcReadOptions.Builder setPartitionUpperBound(long partitionUpperBound) {
            this.partitionUpperBound = partitionUpperBound;
            return this;
        }

        public PhoenixJdbcReadOptions.Builder setNumPartitions(int numPartitions) {
            this.numPartitions = numPartitions;
            return this;
        }

        public PhoenixJdbcReadOptions.Builder setFetchSize(int fetchSize) {
            this.fetchSize = fetchSize;
            return this;
        }

        public PhoenixJdbcReadOptions.Builder setAutoCommit(boolean autoCommit) {
            this.autoCommit = autoCommit;
            return this;
        }

        public PhoenixJdbcReadOptions build() {
            return new PhoenixJdbcReadOptions(
                    this.query,
                    this.partitionColumnName,
                    this.partitionLowerBound,
                    this.partitionUpperBound,
                    this.numPartitions,
                    this.fetchSize,
                    this.autoCommit);
        }
    }
}
