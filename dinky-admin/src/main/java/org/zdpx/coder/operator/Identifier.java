package org.zdpx.coder.operator;

/**
 *
 */
public interface Identifier {
    default String getCode() {
        return getClass().getName();
    }
}
