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

package org.dinky.metric;

import org.dinky.metric.base.MetricService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrometheusService extends MetricService<String> {

    @Override
    protected String formatFlinkJobMetrics(List<HashMap<String, Object>> metrics) {
        StringBuilder sb = new StringBuilder();

        for (HashMap<String, Object> metric : metrics) {
            if (!metric.containsKey("name") || !metric.containsKey("value")) {
                continue;
            }

            String name = metric.get("name").toString().toLowerCase();
            Object value = metric.get("value");

            StringBuilder labels = new StringBuilder();
            for (Map.Entry<String, Object> entry : metric.entrySet()) {
                String key = entry.getKey();
                if ("name".equals(key) || "value".equals(key)) {
                    continue;
                }
                if (labels.length() > 0) {
                    labels.append(",");
                }
                labels.append(key).append("=\"").append(entry.getValue()).append("\"");
            }

            sb.append(name);
            if (labels.length() > 0) {
                sb.append("{").append(labels).append("}");
            }
            sb.append(" ").append(value).append("\n");
        }

        return sb.toString();
    }

    @Override
    protected String mergeFlinkJobMetrics(List<String> metricGroups) {
        return String.join("\n", metricGroups);
    }
}
