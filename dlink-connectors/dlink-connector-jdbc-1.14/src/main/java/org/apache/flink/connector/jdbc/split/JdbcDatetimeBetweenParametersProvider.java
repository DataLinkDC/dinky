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

package org.apache.flink.connector.jdbc.split;

import org.apache.flink.util.Preconditions;

import java.io.Serializable;
import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JdbcDatetimeBetweenParametersProvider
 *
 * @author 金鑫
 * @since 2022/11/18 11:05
 */
public class JdbcDatetimeBetweenParametersProvider implements JdbcParameterValuesProvider {

    protected static final Logger logger = LoggerFactory.getLogger(JdbcDatetimeBetweenParametersProvider.class);
    private final long minVal;
    private final long maxVal;
    private long batchSize;
    private int batchNum;

    /**
     * NumericBetweenParametersProviderJdbc constructor.
     *
     * @param minVal the lower bound of the produced "from" values
     * @param maxVal the upper bound of the produced "to" values
     */
    public JdbcDatetimeBetweenParametersProvider(long minVal, long maxVal) {
        Preconditions.checkArgument(minVal <= maxVal, "minVal must not be larger than maxVal");
        this.minVal = minVal;
        this.maxVal = maxVal;
    }

    /**
     * NumericBetweenParametersProviderJdbc constructor.
     *
     * @param fetchSize the max distance between the produced from/to pairs
     * @param minVal    the lower bound of the produced "from" values
     * @param maxVal    the upper bound of the produced "to" values
     */
    public JdbcDatetimeBetweenParametersProvider(long fetchSize, long minVal, long maxVal) {
        Preconditions.checkArgument(minVal <= maxVal, "minVal must not be larger than maxVal");
        this.minVal = minVal;
        this.maxVal = maxVal;
        ofBatchSize(fetchSize);
    }

    public JdbcDatetimeBetweenParametersProvider ofBatchSize(long batchSize) {
        Preconditions.checkArgument(batchSize > 0, "Batch size must be positive");
        long maxElemCount = (maxVal - minVal) + 1;
        if (batchSize > maxElemCount) {
            batchSize = maxElemCount;
        }
        this.batchSize = batchSize;
        this.batchNum = new Double(Math.ceil((double) maxElemCount / batchSize)).intValue();
        return this;
    }

    public JdbcDatetimeBetweenParametersProvider ofBatchNum(int batchNum) {
        Preconditions.checkArgument(batchNum > 0, "Batch number must be positive");
        long maxElemCount = (maxVal - minVal) + 1;
        if (batchNum > maxElemCount) {
            batchNum = (int) maxElemCount;
        }
        this.batchNum = batchNum;
        this.batchSize = new Double(Math.ceil((double) maxElemCount / batchNum)).longValue();
        return this;
    }

    @Override
    public Serializable[][] getParameterValues() {
        Preconditions.checkState(
                batchSize > 0,
                "Batch size and batch number must be positive. Have you called `ofBatchSize` or `ofBatchNum`?");
        long maxElemCount = (maxVal - minVal) + 1;
        long bigBatchNum = maxElemCount - (batchSize - 1) * batchNum;
        Serializable[][] parameters = new Serializable[batchNum][2];
        long start = minVal;
        for (int i = 0; i < batchNum; i++) {
            long end = start + batchSize - 1 - (i >= bigBatchNum ? 1 : 0);
            parameters[i] = new Date[]{new Date(start), new Date(end)};
            start = end + 1;
        }
        logger.error("时间范围 {}", parameters);
        return parameters;
    }
}
