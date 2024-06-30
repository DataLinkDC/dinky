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

import org.dinky.data.enums.UserType;
import org.dinky.data.model.LdapUserIdentification;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.rbac.User;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LdapContext {
    private static final SystemConfiguration configuration = SystemConfiguration.getInstances();

    /**
     * Get the LDAP context source object.
     *
     * @return LDAP context source object
     */
    public static LdapContextSource getLdapContext() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(configuration.getLdapUrl().getValue());
        contextSource.setBase(configuration.getLdapBaseDn().getValue());
        contextSource.setUserDn(configuration.getLdapUserDn().getValue());
        contextSource.setPassword(configuration.getLdapUserPassword().getValue());
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    /**
     * Get the search controls for LDAP search.
     *
     * @return Search controls for LDAP search
     */
    public static SearchControls getControls() {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        // controls.setCountLimit(ldapConfig.getCountLimit());
        controls.setTimeLimit(configuration.getLdapTimeLimit().getValue());
        return controls;
    }

    /**
     * Context mapper for LDAP user identification.
     */
    public static class UserContextMapper implements ContextMapper<LdapUserIdentification> {

        /**
         * Map the LDAP context to LdapUserIdentification object.
         *
         * @param ctx LDAP context
         * @return LdapUserIdentification object
         */
        public LdapUserIdentification mapFromContext(Object ctx) {
            DirContextOperations adapter = (DirContextOperations) ctx;
            return new LdapUserIdentification(
                    adapter.getNameInNamespace(), adapter.getDn().toString(), adapter.getAttributes());
        }
    }

    /**
     * Attributes mapper from LDAP user to Local user.
     */
    public static class UserAttributesMapperMapper implements AttributesMapper<User> {

        /**
         * Map the LDAP attributes to User object.
         *
         * @param attributes LDAP attributes
         * @return User object
         * @throws NamingException if there is an error during mapping
         */
        @Override
        public User mapFromAttributes(Attributes attributes) throws NamingException {
            Attribute usernameAttr =
                    attributes.get(configuration.getLdapCastUsername().getValue());
            Attribute nicknameAttr =
                    attributes.get(configuration.getLdapCastNickname().getValue());

            if (usernameAttr != null) {
                User user = new User();
                user.setUsername(usernameAttr.get().toString());
                if (nicknameAttr != null) {
                    user.setNickname(nicknameAttr.get().toString());
                }
                user.setUserType(UserType.LDAP.getCode());
                user.setEnabled(true);
                return user;
            } else {
                log.error("LDAP user mapping failed, username attribute is null");
                return null;
            }
        }
    }
}
