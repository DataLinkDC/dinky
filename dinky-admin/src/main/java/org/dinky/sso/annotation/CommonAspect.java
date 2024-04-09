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

package org.dinky.sso.annotation;

import java.util.List;

import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAllRolesAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Common aspect behaviors.
 *
 * @author Jerome Leleu
 * @since 3.2.0
 */
@Component
public class CommonAspect {

    private static final IsAuthenticatedAuthorizer IS_AUTHENTICATED_AUTHORIZER = new IsAuthenticatedAuthorizer();

    @Autowired
    private JEEContext webContext;

    @Autowired
    private ProfileManager profileManager;

    protected List<CommonProfile> isAuthenticated(final boolean readFromSession) {
        final List<CommonProfile> profiles = profileManager.getAll(readFromSession);

        if (!IS_AUTHENTICATED_AUTHORIZER.isAuthorized(webContext, profiles)) {
            throw UnauthorizedAction.INSTANCE;
        }
        return profiles;
    }

    protected void requireAnyRole(final boolean readFromSession, final String... roles) {
        final List<CommonProfile> profiles = isAuthenticated(readFromSession);

        final RequireAnyRoleAuthorizer<CommonProfile> authorizer = new RequireAnyRoleAuthorizer<>(roles);
        if (!authorizer.isAuthorized(webContext, profiles)) {
            throw ForbiddenAction.INSTANCE;
        }
    }

    protected void requireAllRoles(final boolean readFromSession, final String... roles) {
        final List<CommonProfile> profiles = isAuthenticated(readFromSession);

        final RequireAllRolesAuthorizer<CommonProfile> authorizer = new RequireAllRolesAuthorizer<>(roles);
        if (!authorizer.isAuthorized(webContext, profiles)) {
            throw ForbiddenAction.INSTANCE;
        }
    }
}
