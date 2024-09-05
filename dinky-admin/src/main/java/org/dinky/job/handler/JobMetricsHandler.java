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

package org.dinky.job.handler;

import org.dinky.context.MetricsContextHolder;
import org.dinky.data.constant.NetConstant;
import org.dinky.data.model.ext.JobInfoDetail;
import org.dinky.data.vo.MetricsVO;
import org.dinky.utils.HttpUtils;
import org.dinky.utils.TimeUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * The Job Metrics Handler class is used to process operations related to job metrics。
 */
@Slf4j
public class JobMetricsHandler {

    /**
     * The writeFlinkMetrics method is used to write Flink metrics.  </br>
     * Use concurrent programming to get the data of each indicator through asynchronous requests. </br>
     * Send to MetricsContextHolder asynchronously at the end of the method.  </br>
     * Thus, the operation of writing the Flink indicator is completed. </br>
     */
    public static void refreshAndWriteFlinkMetrics(
            JobInfoDetail jobInfoDetail, Map<String, Map<String, String>> customMetricsList) {
        String[] jobManagerUrls =
                jobInfoDetail.getClusterInstance().getJobManagerHost().split(",");
        String jobId = jobInfoDetail.getInstance().getJid();

        // Create a CompletableFuture array for concurrent acquisition of indicator data
        CompletableFuture<?>[] array = customMetricsList.entrySet().stream()
                .map(e -> CompletableFuture.runAsync(
                        () -> fetchFlinkMetrics(e.getKey(), e.getValue(), jobManagerUrls, jobId)))
                .toArray(CompletableFuture[]::new);
        try {
            CompletableFuture.allOf(array).join();
            MetricsVO metricsVO = new MetricsVO();
            metricsVO.setContent(customMetricsList);
            metricsVO.setHeartTime(LocalDateTime.now());
            metricsVO.setModel(jobId);
            metricsVO.setDate(TimeUtil.nowStr("yyyy-MM-dd"));
            MetricsContextHolder.getInstance().saveToSqlite(metricsVO.getModel(), metricsVO);
        } catch (Exception e) {
            log.error("Get and save Flink metrics error", e);
        }
    }

    /**
     * The fetchFlinkMetrics method is used to obtain Flink indicator data.
     *
     * @param v       metric name
     * @param m       metric mapping table
     * @param urlList List of URLs for JobManager
     * @param jid     job ID
     */
    private static void fetchFlinkMetrics(String v, Map<String, String> m, String[] urlList, String jid) {
        if (urlList == null || urlList.length == 0) {
            return;
        }

        String metricsName = String.join(",", m.keySet());
        String urlParam = null;
        try {
            urlParam = String.format(
                    "/jobs/%s/vertices/%s/metrics?get=%s", jid, v, URLEncoder.encode(metricsName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        HttpUtils.request(new ArrayList<>(Arrays.asList(urlList)), urlParam, NetConstant.READ_TIME_OUT, x -> {
            JSONArray array = JSONUtil.parseArray(x.body());
            array.forEach(y -> {
                JSONObject jsonObject = JSONUtil.parseObj(y);
                String id = jsonObject.getStr("id");
                String value = jsonObject.getStr("value");
                m.put(id, value);
            });
        });
    }
}
