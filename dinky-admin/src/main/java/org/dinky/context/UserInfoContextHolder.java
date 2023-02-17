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

package org.dinky.context;

import org.dinky.dto.UserDTO;

import java.util.concurrent.atomic.AtomicReference;

/** UserInfoContextHolder */
public class UserInfoContextHolder {

    private static AtomicReference<UserDTO> USER_INFO_CONTEXT = new AtomicReference<>();

    public static void set(UserDTO value) {
        USER_INFO_CONTEXT.set(value);
    }

    public static UserDTO get() {
        return USER_INFO_CONTEXT.get();
    }

    public static void refresh(UserDTO value) {
        USER_INFO_CONTEXT.getAndUpdate(v -> value);
    }
}
