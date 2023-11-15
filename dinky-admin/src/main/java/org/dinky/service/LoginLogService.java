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

import org.dinky.data.enums.Status;
import org.dinky.data.model.LoginLog;
import org.dinky.data.model.rbac.User;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

public interface LoginLogService extends ISuperService<LoginLog> {

    /**
     * Save the login log for a user.
     *
     * @param user A {@link User} object representing the user to save the login log for.
     * @param status The status of the login operation.
     * @param ip The IP address of the login device.
     * @param msg The message associated with the login operation.
     */
    @Deprecated
    void saveLoginLog(User user, Integer status, String ip, String msg);

    /**
     * Save the login log for a user with only the status and IP address.
     *
     * @param user A {@link User} object representing the user to save the login log for.
     * @param status The status of the login operation.
     * @param ip The IP address of the login device.
     */
    @Deprecated
    void saveLoginLog(User user, Status status, String ip);

    /**
     * Save the login log for a user with only the status.
     *
     * @param user A {@link User} object representing the user to save the login log for.
     * @param status The status of the login operation.
     */
    void saveLoginLog(User user, Status status);

    /**
     * Get the login records for a user.
     *
     * @param userId The ID of the user to get the login records for.
     * @return A list of {@link LoginLog} objects representing the login records for the specified user.
     */
    List<LoginLog> loginRecord(Integer userId);
}
