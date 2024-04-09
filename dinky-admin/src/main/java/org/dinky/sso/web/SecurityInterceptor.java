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

package org.dinky.sso.web;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.JEEContextFactory;
import org.pac4j.core.context.session.JEESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.http.adapter.JEEHttpActionAdapter;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.FindBest;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * <p>This interceptor protects an url.</p>
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    private static final AtomicInteger internalNumber = new AtomicInteger(1);

    private SecurityLogic<Boolean, JEEContext> securityLogic;

    private String clients;

    private String authorizers;

    private String matchers;

    private Boolean multiProfile;

    private Config config;

    private HttpActionAdapter httpActionAdapter;

    public SecurityInterceptor(final Config config) {
        this.config = config;
    }

    public SecurityInterceptor(final Config config, final String clients) {
        this(config);
        this.clients = clients;
    }

    public SecurityInterceptor(final Config config, final String clients, final HttpActionAdapter httpActionAdapter) {
        this.clients = clients;
        this.config = config;
        this.httpActionAdapter = httpActionAdapter;
    }

    public SecurityInterceptor(final Config config, final String clients, final String authorizers) {
        this(config, clients);
        this.authorizers = authorizers;
    }

    public SecurityInterceptor(final Config config, final String clients, final Authorizer[] authorizers) {
        this(config, clients);
        this.authorizers = addAuthorizers(config, authorizers);
    }

    public SecurityInterceptor(
            final Config config, final String clients, final String authorizers, final String matchers) {
        this(config, clients, authorizers);
        this.matchers = matchers;
    }

    public SecurityInterceptor(
            final Config config, final String clients, final Authorizer[] authorizers, final Matcher[] matchers) {
        this(config, clients, addAuthorizers(config, authorizers));
        this.matchers = addMatchers(config, matchers);
    }

    private static String addAuthorizers(final Config config, final Authorizer[] authorizers) {
        final int n = internalNumber.getAndAdd(1);
        final int nbAuthorizers = authorizers.length;
        final StringBuilder names = new StringBuilder("");
        for (int i = 0; i < nbAuthorizers; i++) {
            final String name = "$int_authorizer" + n + "." + i;
            config.addAuthorizer(name, authorizers[i]);
            if (i > 0) {
                names.append(",");
            }
            names.append(name);
        }
        return names.toString();
    }

    private static String addMatchers(final Config config, final Matcher[] matchers) {
        final int n = internalNumber.getAndAdd(1);
        final int nbMatchers = matchers.length;
        final StringBuilder names = new StringBuilder("");
        for (int i = 0; i < nbMatchers; i++) {
            final String name = "$int_matcher" + n + "." + i;
            config.addMatcher(name, matchers[i]);
            if (i > 0) {
                names.append(",");
            }
            names.append(name);
        }
        return names.toString();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        final SessionStore<JEEContext> bestSessionStore = FindBest.sessionStore(null, config, JEESessionStore.INSTANCE);
        final HttpActionAdapter<Boolean, JEEContext> bestAdapter =
                FindBest.httpActionAdapter(httpActionAdapter, config, JEEHttpActionAdapter.INSTANCE);
        final SecurityLogic<Boolean, JEEContext> bestLogic =
                FindBest.securityLogic(securityLogic, config, DefaultSecurityLogic.INSTANCE);

        final JEEContext context = (JEEContext) FindBest.webContextFactory(null, config, JEEContextFactory.INSTANCE)
                .newContext(request, response, bestSessionStore);
        final Object result = bestLogic.perform(
                context,
                config,
                (ctx, profiles, parameters) -> true,
                bestAdapter,
                clients,
                authorizers,
                matchers,
                multiProfile);
        if (result == null) {
            return false;
        }
        return Boolean.parseBoolean(result.toString());
    }

    public SecurityLogic<Boolean, JEEContext> getSecurityLogic() {
        return securityLogic;
    }

    public void setSecurityLogic(final SecurityLogic<Boolean, JEEContext> securityLogic) {
        this.securityLogic = securityLogic;
    }

    public String getClients() {
        return clients;
    }

    public void setClients(final String clients) {
        this.clients = clients;
    }

    public String getAuthorizers() {
        return authorizers;
    }

    public void setAuthorizers(final String authorizers) {
        this.authorizers = authorizers;
    }

    public String getMatchers() {
        return matchers;
    }

    public void setMatchers(final String matchers) {
        this.matchers = matchers;
    }

    public Boolean getMultiProfile() {
        return multiProfile;
    }

    public void setMultiProfile(final Boolean multiProfile) {
        this.multiProfile = multiProfile;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }

    public HttpActionAdapter getHttpActionAdapter() {
        return httpActionAdapter;
    }

    public void setHttpActionAdapter(final HttpActionAdapter httpActionAdapter) {
        this.httpActionAdapter = httpActionAdapter;
    }
}
