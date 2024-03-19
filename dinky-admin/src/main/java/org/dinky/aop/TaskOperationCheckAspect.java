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

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.dinky.data.annotations.CheckTaskOwner;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.dto.TaskRollbackVersionDTO;
import org.dinky.data.dto.TaskSaveDTO;
import org.dinky.data.exception.BusException;
import org.dinky.service.TaskService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Aspect
@Slf4j
@Component
public class TaskOperationCheckAspect {

    @Resource
    private TaskService taskService;

    /**
     * Check whether the user has the permission to perform the task
     *
     * @param joinPoint
     * @param checkTaskOwner
     * @return
     * @throws Throwable
     */
    @Around(value = "@annotation(checkTaskOwner)")
    public Object processAround(ProceedingJoinPoint joinPoint, CheckTaskOwner checkTaskOwner) throws Throwable {
        String[] privilegeRoles = checkTaskOwner.privilegeRoles();
        if(!StpUtil.hasRoleOr(privilegeRoles)){
            Integer id = getId(joinPoint);
            if(Objects.nonNull(id)){
                TaskDTO taskDTO = taskService.getTaskInfoById(id);
                if(Objects.nonNull(taskDTO)){
                    if(!hasPermission(taskDTO.getFirstLevelOwner(),taskDTO.getSecondLevelOwners())){
                        throw new BusException("用户无权限操作任务");
                    }
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

    private Boolean hasPermission(Integer firstLevelOwner,String secondLevelOwners){
        return firstLevelOwner != null && firstLevelOwner == StpUtil.getLoginIdAsInt()
                ||(secondLevelOwners != null && secondLevelOwners.contains(StpUtil.getLoginIdAsString() + ","));
    }

    private Integer getId(ProceedingJoinPoint joinPoint){
        Integer id = null;

        Object[] args = joinPoint.getArgs();
        if(args[0] instanceof Integer){
            id = (Integer) args[0];
        } else if (args[0] instanceof TaskDTO){
            id = ((TaskDTO) args[0]).getId();
        } else if (args[0] instanceof TaskSaveDTO){
            id = ((TaskSaveDTO) args[0]).getId();
        } else if (args[0] instanceof TaskRollbackVersionDTO){
            id = ((TaskRollbackVersionDTO) args[0]).getTaskId();
        }

        return id;
    }
}
