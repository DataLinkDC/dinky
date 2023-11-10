package org.dinky.utils;

import cn.hutool.core.map.MapUtil;
import org.apache.paimon.types.DataType;
import org.apache.paimon.types.DataTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public class PaimonTypeUtil {
    private final static Map<Class<?>, DataType> TYPE_MAP = MapUtil.<Class<?>, DataType>
                    builder(String.class, DataTypes.STRING())
            .put(Integer.class, DataTypes.INT())
            .put(Long.class, DataTypes.BIGINT())
            .put(Double.class, DataTypes.DOUBLE())
            .put(Float.class, DataTypes.FLOAT())
            .put(Boolean.class, DataTypes.BOOLEAN())
            .put(Byte.class, DataTypes.TINYINT())
            .put(Short.class, DataTypes.SMALLINT())
            .put(BigDecimal.class, DataTypes.DECIMAL(30, 4))
            .put(Date.class, DataTypes.TIMESTAMP(3))
            .put(LocalDateTime.class, DataTypes.TIMESTAMP(3))
            .build();

    public static DataType classToDataType(Class<?> clazz) {
        return TYPE_MAP.getOrDefault(clazz, DataTypes.STRING());
    }
}
