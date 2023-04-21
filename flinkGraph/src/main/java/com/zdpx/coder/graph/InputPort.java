package com.zdpx.coder.graph;

/**
 * 输入端口接口
 */
public interface InputPort<S extends PseudoData<S>> extends Port<S> {
    S getPseudoData();

    void setPseudoData(S value);

}
