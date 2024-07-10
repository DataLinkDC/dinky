package org.dinky.metadata.config;

public enum CatalogType {
    Hive("Hive"),
    JDBC("JDBC"),
    FileSystem("FileSystem");

    private final String name;

    CatalogType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public static CatalogType getByName(String name) {
        for (CatalogType catalogType : CatalogType.values()) {
            if (catalogType.getName().equals(name)) {
                return catalogType;
            }
        }
        return null;
    }
}
