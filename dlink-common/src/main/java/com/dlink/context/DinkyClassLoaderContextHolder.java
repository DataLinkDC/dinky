package com.dlink.context;

import com.dlink.classloader.DinkyClassLoader;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ZackYoung
 * @since 0.7.0
 */
@Slf4j
public class DinkyClassLoaderContextHolder {
    private static final ThreadLocal<DinkyClassLoader> CLASS_LOADER_CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<ClassLoader> INIT_CLASS_LOADER_CONTEXT = new ThreadLocal<>();

    public static void set(DinkyClassLoader classLoader) {
        CLASS_LOADER_CONTEXT.set(classLoader);
        INIT_CLASS_LOADER_CONTEXT.set(Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    public static DinkyClassLoader get() {
        return CLASS_LOADER_CONTEXT.get();
    }

    public static void clear() {
        CLASS_LOADER_CONTEXT.remove();
        DinkyClassLoader dinkyClassLoader = get();
        try {
            dinkyClassLoader.close();
        } catch (IOException e) {
            log.error("卸载类失败，reason: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        dinkyClassLoader = null;
        Thread.currentThread().setContextClassLoader(INIT_CLASS_LOADER_CONTEXT.get());
        INIT_CLASS_LOADER_CONTEXT.remove();
    }
}
