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

import org.dinky.data.annotations.CheckTaskApproval;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.utils.AspectUtil;

import java.lang.reflect.Method;
import java.util.Objects;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
public class TaskApprovalAspect {
    @Resource
    private ApplicationContext applicationContext;

    /**
     * Check whether the user has the permission to perform the task.
     *
     * @param joinPoint task operation
     * @param checkTaskApproval check task approval aspect
     * @return join point execute result
     * @throws Throwable exception if task still need approval
     */
    @Around(value = "@annotation(checkTaskApproval)")
    public Object processAround(ProceedingJoinPoint joinPoint, CheckTaskApproval checkTaskApproval) throws Throwable {
        if (SystemConfiguration.getInstances().enableTaskSubmitApprove()) {
            Class checkParam = checkTaskApproval.checkParam();
            Object param = AspectUtil.getParam(joinPoint, checkParam);
            if (Objects.nonNull(param)) {
                Object bean = applicationContext.getBean(checkTaskApproval.checkInterface());
                Class<?> clazz = bean.getClass();
                Method method = clazz.getMethod(checkTaskApproval.checkMethod(), param.getClass());
                Object invoke = method.invoke(bean, param);
                if (invoke != null && (Boolean) invoke) {
                    throw new BusException(Status.SYS_APPROVAL_TASK_NOT_APPROVED);
                }
            }
        }

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throw e;
        }
        return result;
    }
}
