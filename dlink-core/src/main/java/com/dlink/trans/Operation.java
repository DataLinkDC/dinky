package com.dlink.trans;

/**
 * Operation
 *
 * @author wenmo
 * @since 2021/6/13 19:24
 */
public interface Operation {

    String getHandle();

    boolean canHandle(String key);

    void build();
}
