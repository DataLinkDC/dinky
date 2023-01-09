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

package com.dlink.aop;

import com.dlink.classloader.DinkyClassLoader;
import com.dlink.context.DinkyClassLoaderContextHolder;
import com.dlink.job.JobResult;
import com.dlink.process.exception.DinkyException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ZackYoung
 * @since 0.7.0
 */
@Aspect
@Component
@Slf4j
public class UdfClassLoaderAspect {

    @Pointcut("execution(* com.dlink.service.TaskService.*(..))")
    public void taskServicePointcut() {
    }

    @Pointcut("execution(* com.dlink.service.APIService.*(..))")
    public void apiServicePointcut() {
    }

    @Pointcut("execution(* com.dlink.service.StudioService.*(..))")
    public void studioServicePointcut() {
    }

    @Pointcut("apiServicePointcut() || taskServicePointcut() || studioServicePointcut()")
    public void allPointcut() {
    }

    @Around("allPointcut()")
    public Object round(ProceedingJoinPoint proceedingJoinPoint) {
        Object proceed = null;

        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            if (!(e instanceof DinkyException)) {
                throw new DinkyException(e);
            }
        } finally {
            if (proceed instanceof JobResult) {
                ClassLoader lastContextClassLoader = Thread.currentThread().getContextClassLoader();
                if (lastContextClassLoader instanceof DinkyClassLoader) {
                    DinkyClassLoaderContextHolder.clear();
                }
            }
        }
        return proceed;

    }

}
