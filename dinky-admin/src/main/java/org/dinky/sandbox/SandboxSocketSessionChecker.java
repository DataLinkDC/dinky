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

package org.dinky.sandbox;

import org.dinky.assertion.Asserts;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.sandbox.socket.SandboxSessionChecker;

import cn.dev33.satoken.stp.StpUtil;

/**
 * Accepts a Sandbox Socket handshake when the client presents a valid login session
 * or the configured Dinky application token.
 */
public class SandboxSocketSessionChecker implements SandboxSessionChecker {

    @Override
    public boolean accept(String sessionToken) {
        if (Asserts.isNullString(sessionToken)) {
            return false;
        }
        String dinkyToken = SystemConfiguration.getInstances().getDinkyToken().getValue();
        if (Asserts.isNotNullString(dinkyToken) && dinkyToken.equals(sessionToken)) {
            return true;
        }
        try {
            return StpUtil.getLoginIdByToken(sessionToken) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
