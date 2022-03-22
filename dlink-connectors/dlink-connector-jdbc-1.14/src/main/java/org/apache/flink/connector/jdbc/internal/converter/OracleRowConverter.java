package org.apache.flink.connector.jdbc.internal.converter;

import org.apache.flink.table.types.logical.RowType;

/**
 * Runtime converter that responsible to convert between JDBC object and Flink internal object for
 * Oracle.
 *
 * @author wenmo
 * @since 2021/9/19 20:28
 */
public class OracleRowConverter extends AbstractJdbcRowConverter {

    private static final long serialVersionUID = 1L;

    @Override
    public String converterName() {
        return "Oracle";
    }

    public OracleRowConverter(RowType rowType) {
        super(rowType);
    }
}
