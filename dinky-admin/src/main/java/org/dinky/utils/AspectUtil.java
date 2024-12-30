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

package org.dinky.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class AspectUtil {
    public static Object getParam(ProceedingJoinPoint joinPoint, Class paramAnno) throws IllegalAccessException {
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
