package com.dlink.job;

/**
 * JobContextHolder
 *
 * @author wenmo
 * @since 2021/6/26 23:29
 */
public class JobContextHolder {
    private static final ThreadLocal<Job> CONTEXT = new ThreadLocal<>();

    public static void setJob(Job job) {
        CONTEXT.set(job);
    }

    public static Job getJob() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
