package org.apache.flink.connector.phoenix.internal.converter;


import org.apache.flink.table.types.logical.RowType;

/**
 * PhoenixRowConverter
 *
 * @author gy
 * @since 2022/3/16 11:21
 **/
public class PhoenixRowConverter extends AbstractJdbcRowConverter {

    private static final long serialVersionUID = 1L;

    @Override
    public String converterName() {
        return "Phoenix";
    }

    public PhoenixRowConverter(RowType rowType) {
        super(rowType);
    }
}
