package org.dinky.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
