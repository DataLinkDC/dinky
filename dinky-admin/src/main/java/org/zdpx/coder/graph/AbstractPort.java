package org.zdpx.coder.graph;


import org.zdpx.coder.operator.Operator;

/**
 *
 * 节点接口(输入/输出)抽象类, 实现接口的通用功能
 *
 * @author Licho Sun
 * @param <T> 表示接口包含的数据类型
 */
public class AbstractPort<T extends PseudoData<T>> implements Port<T> {
    /**
     * 端口名称
     */
    protected String name;

    /**
     * 端口相关的连接信息
     */
    protected Connection<T> connection;

    /**
     * 端口所在节点
     */
    protected Operator parent;

    public AbstractPort(Operator parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    //region g/s
    @Override
    public Connection<T> getConnection() {
        return connection;
    }

    @Override
    public void setConnection(Connection<T> value) {
        this.connection = value;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Operator getParent() {
        return parent;
    }

    public void setParent(Operator parent) {
        this.parent = parent;
    }
    //endregion
}
