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

import org.dinky.data.model.RowPermissions;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

public interface RowPermissionsService extends ISuperService<RowPermissions> {

    /**
     * delete user role select permissions by role id
     *
     * @param roleIds role id
     * @return delete status
     */
    boolean deleteByRoleIds(List<Integer> roleIds);

    /**
     * select user role data permissions by role id
     *
     * @param roleId role id
     * @return List<RowPermissions>
     */
    List<RowPermissions> listAllByRoleId(Integer roleId);

    /**
     * select user role data permissions by role ids
     *
     * @param roleIds role ids
     * @return List<RowPermissions>
     */
    List<RowPermissions> listRoleSelectPermissionsByRoleIds(List<Integer> roleIds);
}
