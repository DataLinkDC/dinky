package com.zdpx.coder.graph;

/**
 * 输入端口接口
 */
public interface OutputPort<T extends PseudoData<T>> extends Port<T> {
    T getPseudoData();

    void  setPseudoData(T value);
}
