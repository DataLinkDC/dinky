package org.dinky.zdpx.coder.graph;


import org.dinky.zdpx.coder.operator.Operator;
import org.dinky.zdpx.coder.utils.Preconditions;

/**
 * 输入端口
 *
 * @author Licho Sun
 */
public class InputPortObject<T extends PseudoData<T>> extends AbstractPort<T> implements InputPort<T> {

    /**
     * 接口可接受类型
     */
    T pseudoData;

    public InputPortObject(Operator parent, String name) {
        super(parent, name);
    }

    @Override
    public T getPseudoData() {
        return pseudoData;
    }

    @Override
    public void setPseudoData(T value) {
        this.pseudoData = value;
    }

    public OutputPort<T> getOutputPort() {
        Connection<T> connection = getConnection();
        Preconditions.checkNotNull(connection, String.format("Operator %s input not connection.",
                this.getParent().getName()));
        return getConnection().getFromPort();
    }

    /**
     * 获取连接另一节点的输出数据信息
     *
     * @return 连接另一节点的输出数据信息
     */
    public T getOutputPseudoData() {
        OutputPort<T> fromPort = getOutputPort();
        Preconditions.checkNotNull(fromPort, String.format("Operator %String input can not get sender port.",
                this.getName()));
        return fromPort.getPseudoData();
    }
}
