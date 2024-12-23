package org.dinky.aop;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.dinky.data.annotations.CheckTaskApproval;
import org.dinky.data.annotations.CheckTaskOwner;
import org.dinky.data.constant.BaseConstant;
import org.dinky.data.enums.Status;
import org.dinky.data.enums.TaskOwnerLockStrategyEnum;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.utils.AspectUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Objects;

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
