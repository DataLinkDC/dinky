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

package org.dinky.data.result;

import org.dinky.utils.JsonUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Sets;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * SelectResult
 *
 * @since 2021/5/25 16:01
 */
@Slf4j
@Setter
@Getter
@NoArgsConstructor
public class SelectResult extends AbstractResult implements IResult {

    private String jobID;
    private List<Map<String, Object>> rowData;
    private Integer total;
    private Integer currentCount;
    private LinkedHashSet<String> columns;
    private boolean isDestroyed;
    private boolean truncationFlag = false;

    public SelectResult(
            List<Map<String, Object>> rowData,
            Integer total,
            Integer currentCount,
            LinkedHashSet<String> columns,
            String jobID,
            boolean success) {
        this.rowData = rowData;
        this.total = total;
        this.currentCount = currentCount;
        this.columns = columns;
        this.jobID = jobID;
        this.success = success;
        this.isDestroyed = false;
    }

    public SelectResult(String jobID, List<Map<String, Object>> rowData, LinkedHashSet<String> columns) {
        this.jobID = jobID;
        this.rowData = rowData;
        this.total = rowData.size();
        this.columns = columns;
        this.success = true;
        this.isDestroyed = false;
    }

    public SelectResult(String jobID, boolean isDestroyed, boolean success) {
        this.jobID = jobID;
        this.isDestroyed = isDestroyed;
        this.success = success;
        this.endTime = LocalDateTime.now();
    }

    @Override
    public String getJobId() {
        return jobID;
    }

    /**
     * Get the json truncated to the specified length.
     *
     * @param length truncate length
     * @return json string
     */
    public String toTruncateJson(Long length) {
        String jsonStr = JsonUtils.toJsonString(this);
        long overLength = jsonStr.length() - length;
        if (overLength <= 0) {
            return jsonStr;
        }
        this.truncationFlag = true;
        if (CollectionUtil.isEmpty(rowData)) {
            this.columns = Sets.newLinkedHashSet();
            String finalJsonStr = JsonUtils.toJsonString(this);
            if (finalJsonStr.length() > length) {
                log.warn(
                        "The row data and columns is empty, but still exceeds the length limit. "
                                + "Json: {}, length: {}",
                        finalJsonStr,
                        length);
                return "{}";
            }
            return finalJsonStr;
        }
        // Estimate the size of each row of data to determine how many rows should be removed.
        String lineJsonStr = JsonUtils.toJsonString(rowData.get(rowData.size() - 1));
        int lineLength = lineJsonStr.length();
        int removeLine = getRemoveLine(overLength, lineLength, rowData.size());
        rowData = ListUtil.sub(rowData, 0, rowData.size() - removeLine);
        return toTruncateJson(length);
    }

    private int getRemoveLine(long overLength, int lineLength, int rowDataSize) {
        int removeLine = (int) (overLength / lineLength);
        if (removeLine < 1) {
            return 1;
        }
        return Math.min(removeLine, rowDataSize);
    }

    public static SelectResult buildDestruction(String jobID) {
        return new SelectResult(jobID, true, false);
    }

    public static SelectResult buildSuccess(String jobID) {
        return new SelectResult(jobID, false, true);
    }

    public static SelectResult buildFailed() {
        return new SelectResult(null, false, false);
    }
}
