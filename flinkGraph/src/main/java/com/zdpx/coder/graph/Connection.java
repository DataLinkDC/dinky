package com.zdpx.coder.graph;


import com.zdpx.coder.operator.TableInfo;

/**
 * 表示节点间的连接关系,{@link OutputPort <T> fromPort} 表示源节点的输出端口, {@link InputPort <T> toPort} 表示目的节点的输入端口,
 * 并在此类注入转换功能.
 *
 * @param <T> 目前类型统一为{@link TableInfo TableInfo}, 将来可以为不一致类型,
 * @author Licho Sun
 */
public class Connection<T extends PseudoData<T>> {

    /**
     * 源端口
     */
    private OutputPort<T> fromPort;
    /**
     * 目标端口
     */
    private InputPort<T> toPort;

//region g/s

    public OutputPort<T> getFromPort() {
        return fromPort;
    }

    public void setFromPort(OutputPort<T> fromPort) {
        this.fromPort = fromPort;
    }

    public InputPort<T> getToPort() {
        return toPort;
    }

    public void setToPort(InputPort<T> toPort) {
        this.toPort = toPort;
    }

//endregion
}
