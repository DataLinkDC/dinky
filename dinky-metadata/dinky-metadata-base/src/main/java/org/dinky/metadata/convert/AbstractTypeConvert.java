package org.dinky.metadata.convert;

import org.dinky.assertion.Asserts;
import org.dinky.data.enums.ColumnType;
import org.dinky.data.model.Column;
import org.dinky.metadata.driver.DriverConfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class AbstractTypeConvert implements ITypeConvert {
    protected Map<String, BiFunction<Column, DriverConfig, Optional<ColumnType>>> convertMap = new LinkedHashMap<>();

    @Override
    public String convertToDB(ColumnType columnType) {
        return null;
    }

    @Override
    public ColumnType convert(Column column) {
        return convert(column, null);
    }

    @Override
    public ColumnType convert(Column column, DriverConfig driverConfig) {
        if (Asserts.isNull(column)) {
            return ColumnType.STRING;
        }
        for (Map.Entry<String, BiFunction<Column, DriverConfig, Optional<ColumnType>>> entry : convertMap.entrySet()) {
            if (column.getType().contains(entry.getKey())) {
                Optional<ColumnType> columnType = entry.getValue().apply(column, driverConfig);
                if (columnType.isPresent()) {
                    return columnType.get();
                }
            }
        }

        return ColumnType.STRING;
    }


    protected static Optional<ColumnType> getColumnType(Column column, ColumnType type) {
        return getColumnType(column,  type, type);
    }

    protected static Optional<ColumnType> getColumnType(Column column, ColumnType notNullType,
                                                        ColumnType nullType) {
        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        if (isNullable) {
            return Optional.of(nullType);
        }
        return Optional.of(notNullType);
    }
}
