package com.dlink.job;

/**
 * RunTime
 *
 * @author wenmo
 * @since 2021/6/27 18:06
 */
public abstract class RunTime {

    abstract boolean init();

    abstract boolean ready();

    abstract boolean success();

    abstract boolean error();

    abstract boolean close();
}
