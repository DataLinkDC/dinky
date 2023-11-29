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

package org.dinky.service;

import org.dinky.data.model.alert.AlertHistory;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

/** AlertHistoryService */
public interface AlertHistoryService extends ISuperService<AlertHistory> {

    /**
     * delete alert history by alert group id
     *
     * @param alertGroupId {@link Integer}
     * @return {@link Boolean}
     */
    @Deprecated
    Boolean deleteByAlertGroupId(Integer alertGroupId);

    /**
     * Query alert history records by job instance ID.
     *
     * @param jobInstanceId The ID of the job instance for which to query alert history records.
     * @return A list of {@link AlertHistory} objects representing the alert history records for the specified job instance.
     */
    List<AlertHistory> queryAlertHistoryRecordByJobInstanceId(Integer jobInstanceId);
}
