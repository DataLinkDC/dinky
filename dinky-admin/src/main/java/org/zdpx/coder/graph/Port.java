package org.zdpx.coder.graph;


import org.zdpx.coder.operator.Operator;

/**
 *
 * Operators in a process are connected via input and output ports. Whenever an operator generates
 * output (or metadata about output), it is delivered to the connected input port.
 * <p>
 * This interface defines all behavior and properties common to input and output ports. This is
 * basically names, description etc., as well as adding messages about problems in the process setup
 * and quick fixes.
 *
 * @author Licho Sun
 * @param <T> the type that port can contains.
 */
public interface Port<T extends PseudoData<T>> {
    /**
     * get port name
     *
     * @return port name, the name is not null
     */
    String getName();

    /**
     * set port name
     * @param name the name format according to java variable name.
     */
    void setName(String name);

    /**
     * get operator that port in
     * @return operator that port in
     */
    Operator getParent();

    /**
     * set operator that port in
     * @param parent operator that port in, not null
     */
    void setParent(Operator parent);

    /**
     * get {@link Connection connection} between ports.
     * @return {@link Connection connection} between ports.
     */
    Connection<T> getConnection();

    /**
     * set {@link Connection connection} between ports.
     *
     * @param value {@link Connection connection} between ports.
     */
    void setConnection(Connection<T> value);
}
