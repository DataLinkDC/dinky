package com.dlink.aop;

import java.net.URLClassLoader;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader;
import org.springframework.stereotype.Component;

import com.sun.appserv.ClassLoaderUtil;


/**
 * @author ZackYoung
 * @since 0.7.0
 */
@Aspect
@Component
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
        ClassLoader initContextClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            ClassLoader lastContextClassLoader = Thread.currentThread().getContextClassLoader();
            if (!(lastContextClassLoader instanceof TomcatEmbeddedWebappClassLoader)) {
                if (lastContextClassLoader.getParent() instanceof TomcatEmbeddedWebappClassLoader) {
                    ClassLoaderUtil.releaseLoader((URLClassLoader) lastContextClassLoader);
                    Thread.currentThread().setContextClassLoader(initContextClassLoader);
                }
            }
        }

        return proceed;

    }

}
