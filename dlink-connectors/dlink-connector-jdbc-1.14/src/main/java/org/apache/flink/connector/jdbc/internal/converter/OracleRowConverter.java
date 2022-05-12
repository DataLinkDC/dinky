package org.apache.flink.connector.jdbc.internal.converter;

import oracle.sql.CLOB;

import org.apache.flink.table.data.DecimalData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.data.TimestampData;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.RowType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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

    @Override
    protected JdbcDeserializationConverter createInternalConverter(LogicalType type) {
        switch (type.getTypeRoot()) {
            case NULL:
                return val -> null;
            case BOOLEAN:
            case FLOAT:
            case DOUBLE:
            case INTERVAL_YEAR_MONTH:
            case INTERVAL_DAY_TIME:
                return val -> val;
            case TINYINT:
                return val -> ((Integer) val).byteValue();
            case SMALLINT:
                // Converter for small type that casts value to int and then return short value,
                // since
                // JDBC 1.0 use int type for small values.
                return val -> val instanceof Integer ? ((Integer) val).shortValue() : val;
            case INTEGER:
            case BIGINT:
                return val -> val;
            case DECIMAL:
                final int precision = ((DecimalType) type).getPrecision();
                final int scale = ((DecimalType) type).getScale();
                // using decimal(20, 0) to support db type bigint unsigned, user should define
                // decimal(20, 0) in SQL,
                // but other precision like decimal(30, 0) can work too from lenient consideration.
                return val ->
                    val instanceof BigInteger
                        ? DecimalData.fromBigDecimal(
                        new BigDecimal((BigInteger) val, 0), precision, scale)
                        : DecimalData.fromBigDecimal((BigDecimal) val, precision, scale);
            case DATE:
                return val -> (int) (((Date) val).toLocalDate().toEpochDay());
            case TIME_WITHOUT_TIME_ZONE:
                return val -> (int) (((Time) val).toLocalTime().toNanoOfDay() / 1_000_000L);
            case TIMESTAMP_WITH_TIME_ZONE:
            case TIMESTAMP_WITHOUT_TIME_ZONE:
                return (val) -> {
                    if(val instanceof LocalDateTime){
                        return TimestampData.fromLocalDateTime((LocalDateTime)val);
                    }else if(val instanceof oracle.sql.TIMESTAMP){
                        return TimestampData.fromTimestamp(Timestamp.valueOf(((oracle.sql.TIMESTAMP) val).stringValue()));
                    }else{
                        return TimestampData.fromTimestamp((Timestamp) val);
                    }
                };
            case CHAR:
            case VARCHAR:
                return (val) -> {
                    if(val instanceof CLOB){
                        CLOB clob = (CLOB) val;
                        return  StringData.fromString(clob == null ? null : clob.stringValue());
                    }else {
                        return StringData.fromString((String) val);
                    }
                };
            case BINARY:
            case VARBINARY:
                return val -> val;
            case ARRAY:
            case ROW:
            case MAP:
            case MULTISET:
            case RAW:
            default:
                throw new UnsupportedOperationException("Unsupported type:" + type);
        }
    }
}
