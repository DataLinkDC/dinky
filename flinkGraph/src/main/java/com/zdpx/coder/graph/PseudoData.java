package com.zdpx.coder.graph;

/**
 * 表示在节点间传输数据的基类型, {@link #getType()} 用于获取类型的{@link DataType}枚举表示.
 *
 * @param <T> 自表示模式.
 * @author Licho Sun
 */
public interface PseudoData<T extends PseudoData<T>> {
    /**
     * 获取数据类型表示
     * @return
     */
    DataType getType();

}
