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

package org.dinky.service.impl;

import org.springframework.ldap.support.LdapEncoder;

/**
 * Builds the LDAP login search filter from a well-defined pattern.
 */
public final class LdapLoginFilter {

    private LdapLoginFilter() {}

    /**
     * Build the login search filter, escaping special characters in the username value.
     *
     * @param configuredFilter the configured LDAP filter
     * @param usernameAttribute the attribute that maps to the Dinky username
     * @param username the login username used as a filter value
     * @return the login search filter
     */
    public static String build(String configuredFilter, String usernameAttribute, String username) {
        String encodedUsername = LdapEncoder.filterEncode(username);
        return String.format("(&%s(%s=%s))", configuredFilter, usernameAttribute, encodedUsername);
    }
}
