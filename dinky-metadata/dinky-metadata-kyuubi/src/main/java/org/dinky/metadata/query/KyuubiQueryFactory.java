package org.dinky.metadata.query;

public class KyuubiQueryFactory {
    public static IDBQuery getKyuubiQuery(String databaseType) {
        switch (databaseType) {
            case "Spark":
                return new SparkEngineQuery();
            case "Hive":
                return new HiveEngineQuery();
            case "Trino":
                return new TrinoEngineQuery();
            case "Flink":
                return new FlinkEngineQuery();
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }
    }
}
