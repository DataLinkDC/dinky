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

import org.dinky.context.ConsoleContextHolder;
import org.dinky.process.annotations.ExecuteProcess;
import org.dinky.process.annotations.ProcessId;
import org.dinky.process.annotations.ProcessStep;
import org.dinky.process.enums.ProcessStatus;
import org.dinky.process.enums.ProcessStepType;
import org.dinky.process.enums.ProcessType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
public class ProcessAspect {

    public static String PROCESS_NAME = "name";
    public static String PROCESS_STEP = "step";
    public ConsoleContextHolder contextHolder = ConsoleContextHolder.getInstances();

    /**
     * Block all {@link ExecuteProcess} annotations,
     * As the beginning of the process, set all initialization information
     */
    @Around(value = "@annotation(executeProcess)")
    public Object processAround(ProceedingJoinPoint joinPoint, ExecuteProcess executeProcess) throws Throwable {

        Object result;
        Object processId = getProcessId(joinPoint);
        String name = executeProcess.type() + String.valueOf(processId);
        ProcessType type = executeProcess.type();
        contextHolder.registerProcess(type, name);
        MDC.put(PROCESS_NAME, name);

        try {
            result = joinPoint.proceed();
            contextHolder.finishedProcess(name, ProcessStatus.FINISHED, null);
        } catch (Throwable e) {
            contextHolder.finishedProcess(name, ProcessStatus.FAILED, e);
            throw e;
        } finally {
            // Note that this must be cleaned up，Otherwise, the situation of OOM may occur
            MDC.clear();
        }
        return result;
    }

    /**
     * Block all {@link ProcessStep} annotations,
     * As a specific task step
     */
    @Around(value = "@annotation(processStep)")
    public Object processStepAround(ProceedingJoinPoint joinPoint, ProcessStep processStep) throws Throwable {

        Object result;
        // Record the current step and restore it after the execution is completed
        String parentStep = MDC.get(PROCESS_STEP);
        ProcessStepType processStepType = processStep.type();
        MDC.put(PROCESS_STEP, processStepType.getValue());
        contextHolder.registerProcessStep(processStepType, MDC.get(PROCESS_NAME), parentStep);

        try {
            result = joinPoint.proceed();
            contextHolder.finishedStep(MDC.get(PROCESS_NAME), processStepType, ProcessStatus.FINISHED, null);
        } catch (Exception e) {
            contextHolder.finishedStep(MDC.get(PROCESS_NAME), processStepType, ProcessStatus.FAILED, e);
            throw e;
        } finally {
            // If a parent step exists, it is restored after the execution is complete
            if (parentStep != null) {
                MDC.put(PROCESS_STEP, parentStep);
            }
        }
        return result;
    }

    private Object getProcessId(ProceedingJoinPoint joinPoint) {
        Object[] params = joinPoint.getArgs();
        if (params.length == 0) {
            throw new IllegalArgumentException("Must have ProcessId params");
        }

        // Get the method, here you can convert the signature strong to MethodSignature
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Object param = params[i];
            Annotation[] paramAnn = annotations[i];
            for (Annotation annotation : paramAnn) {
                if (annotation instanceof ProcessId) {
                    return param;
                }
            }
        }
        throw new IllegalArgumentException("Must have ProcessId annoation params");
    }
}
