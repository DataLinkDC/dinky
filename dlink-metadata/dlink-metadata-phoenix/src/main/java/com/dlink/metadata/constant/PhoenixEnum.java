package com.dlink.metadata.constant;

/**
 * @author lcg
 * @operate Phoenix常用数据类型及对应code
 * @date 2022/2/16 16:49
 * @return
 */
public enum PhoenixEnum {
    INTEGER(4),
    BIGINT(-5),
    TINYINT(-6),
    SMALLINT(5),
    FLOAT(6),
    DOUBLE(8),
    DECIMAL(3),
    BOOLEAN(16),
    TIME(92),
    DATE(91),
    TIMESTAMP(93),
    VARCHAR(12),
    CHAR(1),
    BINARY(-2),
    VARBINARY(-3);

    int dataTypeCode;

    PhoenixEnum(int dataTypeCode) {
        this.dataTypeCode = dataTypeCode;
    }

    public int getDataTypeCode() {
        return dataTypeCode;
    }

    /**
     * 获取数字 对应的数据类型  默认返回VARCHAR（无对应）  ， 传参为空时返回为null
     *
     * @param dataTypeCode
     * @return
     */
    public static PhoenixEnum getDataTypeEnum(Integer dataTypeCode) {
        if (dataTypeCode == null) {
            return null;
        } else {
            for (PhoenixEnum typeEnum : PhoenixEnum.values()) {
                if (dataTypeCode.equals(typeEnum.dataTypeCode)) {
                    return typeEnum;
                }
            }
        }
        return PhoenixEnum.VARCHAR;
    }
}
