package org.dinky.zdpx.coder.graph;


import org.dinky.zdpx.coder.operator.Operator;

/**
 * 输出端口
 */
public class OutputPortObject<T extends PseudoData<T>> extends AbstractPort<T> implements OutputPort<T> {

    private T tableInfo;
    public OutputPortObject(Operator parent, String name) {
        super(parent, name);
    }

    @Override
    public T getPseudoData() {
        return tableInfo;
    }

    @Override
    public void setPseudoData(T value) {
        tableInfo = value;
    }
}
