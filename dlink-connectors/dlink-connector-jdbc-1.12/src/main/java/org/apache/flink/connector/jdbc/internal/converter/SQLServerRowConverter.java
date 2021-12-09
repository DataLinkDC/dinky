package org.apache.flink.connector.jdbc.internal.converter;

import org.apache.flink.table.types.logical.RowType;

/**
 * SQLServerRowConverter
 *
 * @author wenmo
 * @since 2021/12/9
 **/
public class SQLServerRowConverter extends AbstractJdbcRowConverter {

    private static final long serialVersionUID = 1L;

    @Override
    public String converterName() {
        return "SQLServer";
    }

    public SQLServerRowConverter(RowType rowType) {
        super(rowType);
    }
}
