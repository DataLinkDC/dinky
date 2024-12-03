package org.dinky.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CatalogTypeMappingEnum {

    DINKY_MYSQL("dinky_mysql", "mysql"),

    DINKY_POSTGRES("dinky_postgres", "pgsql"),

    DINKY_H2("dinky_mysql", "h2"),
    ;

    private final String catalogTypeName;
    private final String dbType;


    public static CatalogTypeMappingEnum ofDbType(String dbType) {
        for (CatalogTypeMappingEnum value : values()) {
            if (value.getDbType().equals(dbType)) {
                return value;
            }
        }
        throw new IllegalArgumentException("The corresponding database type could not be found");
    }

    public static CatalogTypeMappingEnum getCatalogTypeEnum(CatalogTypeMappingEnum catalogType) {
        for (CatalogTypeMappingEnum value : values()) {
            if (value.getCatalogTypeName().equals(catalogType.getCatalogTypeName())) {
                return value;
            }
        }
        throw new IllegalArgumentException("The corresponding database type could not be found");
    }

    public static String getCatalogTypeValue(CatalogTypeMappingEnum catalogType) {
        for (CatalogTypeMappingEnum value : values()) {
            if (value.getCatalogTypeName().equals(catalogType.getCatalogTypeName())) {
                return value.getDbType();
            }
        }
        throw new IllegalArgumentException("The corresponding database type could not be found");
    }

}
