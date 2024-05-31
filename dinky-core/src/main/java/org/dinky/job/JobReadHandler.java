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

package org.dinky.job;

import org.dinky.data.result.SelectResult;

/**
 * JobReadHandler.
 * <p>
 * Because the job is separated into write and read operations,
 * JobReadHandler should not rely on any job-related information except the job id.
 *
 * @since 2024/5/31 11:19
 */
public interface JobReadHandler {

    /**
     * Read result data from persistent storage.
     *
     * @param jobId job id
     * @return result data
     */
    SelectResult readResultDataFromStorage(Integer jobId);
}
