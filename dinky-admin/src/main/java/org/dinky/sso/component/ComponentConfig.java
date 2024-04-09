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

package org.dinky.sso.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.JEESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.FindBest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/**
 * The configuration of the pac4j components.
 *
 * @author Jerome Leleu
 * @since 3.2.0
 */
@Configuration
public class ComponentConfig {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    @Autowired(required = false)
    protected Config config;

    @Autowired(required = false)
    protected SessionStore<JEEContext> sessionStore;

    protected SessionStore<JEEContext> getSessionStore() {
        return FindBest.sessionStore(sessionStore, config, JEESessionStore.INSTANCE);
    }

    @Bean
    @RequestScope
    public JEEContext getWebContext() {
        return new JEEContext(request, response, getSessionStore());
    }

    @Bean
    @RequestScope
    public ProfileManager getProfileManager() {
        return new ProfileManager<>(getWebContext());
    }
}
