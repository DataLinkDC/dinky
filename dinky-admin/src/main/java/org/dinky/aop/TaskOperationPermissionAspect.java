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

import org.dinky.data.annotations.CheckTaskOwner;
import org.dinky.data.constant.BaseConstant;
import org.dinky.data.enums.Status;
import org.dinky.data.enums.TaskOwnerLockStrategyEnum;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.SystemConfiguration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
public class TaskOperationPermissionAspect {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * Check whether the user has the permission to perform the task.
     *
     * @param joinPoint
     * @param checkTaskOwner
     * @return
     * @throws Throwable
     */
    @Around(value = "@annotation(checkTaskOwner)")
    public Object processAround(ProceedingJoinPoint joinPoint, CheckTaskOwner checkTaskOwner) throws Throwable {
        if (!TaskOwnerLockStrategyEnum.ALL.equals(
                        SystemConfiguration.getInstances().GetTaskOwnerLockStrategyValue())
                && BaseConstant.ADMIN_ID != StpUtil.getLoginIdAsInt()) {
            Class checkParam = checkTaskOwner.checkParam();
            Object param = getParam(joinPoint, checkParam);
            if (Objects.nonNull(param)) {
                Object bean = applicationContext.getBean(checkTaskOwner.checkInterface());
                Class<?> clazz = bean.getClass();
                Method method = clazz.getMethod(checkTaskOwner.checkMethod(), param.getClass());
                Object invoke = method.invoke(bean, param);
                if (invoke != null && !(Boolean) invoke) {
                    throw new BusException(Status.TASK_NOT_OPERATE_PERMISSION);
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

    private Object getParam(ProceedingJoinPoint joinPoint, Class paramAnno) throws IllegalAccessException {
        Object[] params = joinPoint.getArgs();
        if (params.length == 0) {
            return null;
        }

        Object paramObj = null;
        // Get the method, here you can convert the signature strong to MethodSignature
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Object param = params[i];
            if (param == null) {
                continue;
            }
            Annotation[] paramAnn = annotations[i];
            for (Annotation annotation : paramAnn) {
                if (annotation.annotationType() == paramAnno) {
                    paramObj = param;
                    break;
                }
            }
            if (paramObj == null) {
                Field[] fields = param.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(paramAnno)) {
                        field.setAccessible(true);
                        paramObj = field.get(param);
                        break;
                    }
                }
            }
        }
        return paramObj;
    }
}
