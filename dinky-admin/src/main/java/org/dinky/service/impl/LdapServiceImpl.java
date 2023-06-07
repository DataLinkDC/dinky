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

import org.dinky.data.dto.LoginDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.AuthException;
import org.dinky.data.model.LdapUserIdentification;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.User;
import org.dinky.service.LdapService;

import java.util.List;

import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LdapServiceImpl implements LdapService {

    SystemConfiguration configuration = SystemConfiguration.getInstances();

    /**
     * Authenticates the user based on the provided login credentials.
     *Throws AuthException if authentication fails.
     * @param loginDTO The login user info
     * @return ldap user
     **/
    @Override
    public User authenticate(LoginDTO loginDTO) throws AuthException {
        LdapTemplate ldapTemplate = new LdapTemplate(getLdapContext());
        // Build LDAP filter, The LdapCastUsername is map to Dinky UserName
        String filter =
                String.format(
                        "(&%s(%s=%s))",
                        configuration.getLdapFilter().getValue(),
                        configuration.getLdapCastUsername().getValue(),
                        loginDTO.getUsername()
                );
        // Perform search operation, we have alreday config baseDn in global
        // so the param base has config ""
        List<LdapUserIdentification> result =
                ldapTemplate.search("", filter, getControls(), new UserContextMapper());
        // Only if the returned result is one is correct,
        // otherwise the corresponding exception is thrown
        if (result.size() == 0) {
            log.info(
                    String.format(
                            "No results found for search, base: '%s'; filter: '%s'",
                            configuration.getLdapBaseDn(), filter));
            throw new AuthException(Status.USER_NOT_EXIST);
        } else if (result.size() > 1) {
            log.error(
                    String.format(
                            "IncorrectResultSize, base: '%s'; filter: '%s'",
                            configuration.getLdapBaseDn(), filter));
            throw new AuthException(Status.LDAP_USER_DUPLICAT);
        } else {
            LdapUserIdentification ldapUserIdentification = result.get(0);
            try {
                Attributes attributes = ldapUserIdentification.getAttributes();
                // Validate username and password
                getLdapContext()
                        .getContext(ldapUserIdentification.getAbsoluteDn(), loginDTO.getPassword());
                //If no exception is thrown, then the login is successful，
                //Build the User with cast
                User user = new User();
                user.setUsername(loginDTO.getUsername());
                user.setNickname(
                        attributes
                                .get(configuration.getLdapCastNickname().getValue())
                                .get()
                                .toString());
                return user;
            } catch (Exception e) {
                if (e instanceof AuthenticationException) {
                    throw new AuthException(e.getCause(), Status.USER_NAME_PASSWD_ERROR);
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private LdapContextSource getLdapContext() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(configuration.getLdapUrl().getValue());
        contextSource.setBase(configuration.getLdapBaseDn().getValue());
        contextSource.setUserDn(configuration.getLdapUserDn().getValue());
        contextSource.setPassword(configuration.getLdapUserPassword().getValue());
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    private SearchControls getControls() {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        // controls.setCountLimit(ldapConfig.getCountLimit());
        controls.setTimeLimit(configuration.getLdapTimeLimit().getValue());
        return controls;
    }

    private static class UserContextMapper implements ContextMapper<LdapUserIdentification> {
        public LdapUserIdentification mapFromContext(Object ctx) {
            DirContextOperations adapter = (DirContextOperations) ctx;
            return new LdapUserIdentification(
                    adapter.getNameInNamespace(),
                    adapter.getDn().toString(),
                    adapter.getAttributes());
        }
    }
}
