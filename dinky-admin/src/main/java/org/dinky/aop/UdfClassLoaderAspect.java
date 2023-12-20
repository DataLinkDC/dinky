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

package org.dinky.aop;

import org.dinky.data.exception.DinkyException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/** @since 0.7.0 */
@Aspect
@Component
@Slf4j
public class UdfClassLoaderAspect {

    @Pointcut("execution(* org.dinky.service.TaskService.*(..))")
    public void taskServicePointcut() {}

    @Pointcut("execution(* org.dinky.service.APIService.*(..))")
    public void apiServicePointcut() {}

    @Pointcut("execution(* org.dinky.service.StudioService.*(..))")
    public void studioServicePointcut() {}

    @Pointcut("apiServicePointcut() || taskServicePointcut() || studioServicePointcut()")
    public void allPointcut() {}

    @Around("allPointcut()")
    public Object round(ProceedingJoinPoint proceedingJoinPoint) {
        Object proceed = null;
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            if (!(e instanceof DinkyException)) {
                throw new DinkyException(e);
            }
            e.printStackTrace();
            throw (DinkyException) e;
        } finally {
            if (contextClassLoader != Thread.currentThread().getContextClassLoader()) {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
        }
        return proceed;
    }
}
