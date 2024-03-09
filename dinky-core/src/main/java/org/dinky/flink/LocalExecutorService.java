package org.dinky.flink;

import org.dinky.classloader.DinkyClassLoader;

import java.lang.ref.WeakReference;

public class LocalExecutorService implements DinkyExecutor {
    private final WeakReference<DinkyClassLoader> dinkyClassLoader;

    public LocalExecutorService() {
        dinkyClassLoader =  new WeakReference<>(DinkyClassLoader.build());
    }

    @Override
    public void init() {

    }
}
