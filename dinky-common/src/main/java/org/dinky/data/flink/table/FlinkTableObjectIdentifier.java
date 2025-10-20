package org.dinky.data.flink.table;

import lombok.Getter;

@Getter
public class FlinkTableObjectIdentifier {
    private final String catalogName;
    private final String databaseName;
    private final String objectName;

    public static FlinkTableObjectIdentifier of(String catalogName, String databaseName, String objectName) {
        return new FlinkTableObjectIdentifier(catalogName, databaseName, objectName);
    }

    public static FlinkTableObjectIdentifier of(String objectName) {
        return of(null, null, objectName);
    }

    private FlinkTableObjectIdentifier(String catalogName, String databaseName, String objectName) {
        this.catalogName = catalogName;
        this.databaseName = databaseName;
        this.objectName = objectName;
        if (objectName == null) {
            throw new IllegalArgumentException("objectName can not be null");
        }
    }

    /**
     *
     * @return catalogName.`databaseName`.`objectName`
     */
    public String toTablePath() {
        StringBuilder sb = new StringBuilder();
        if (catalogName != null) {
            sb.append(catalogName);
        }
        if (databaseName != null) {
            sb.append(".`");
            sb.append(databaseName);
            sb.append("`.");
        }
        sb.append("`");
        sb.append(objectName);
        sb.append("`");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toTablePath();
    }
}
