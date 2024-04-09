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

package org.dinky.sso.annotation.ui;

import org.dinky.sso.annotation.CommonAspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * The aspect to define the web applications annotations.
 *
 * @author Jerome Leleu
 * @since 3.2.0
 */
@Aspect
public class UIAnnotationAspect extends CommonAspect {

    @Before("@annotation(requireAnyRole)")
    public void beforeRequireAnyRole(final RequireAnyRole requireAnyRole) {
        requireAnyRole(true, requireAnyRole.value());
    }

    @Before("@annotation(requireAllRoles)")
    public void beforeRequireAllRoles(final RequireAllRoles requireAllRoles) {
        requireAllRoles(true, requireAllRoles.value());
    }
}
