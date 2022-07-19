package com.dlink.sql;

/**
 * AbstractFlinkQuery
 *
 * @author wenmo
 * @since 2022/7/18 18:43
 **/
public class FlinkQuery {

    public static String separator() {
        return ";\r\n";
    }

    public static String defaultCatalog() {
        return "default_catalog";
    }

    public static String defaultDatabase() {
        return "default_database";
    }

    public static String showCatalogs() {
        return "SHOW CATALOGS";
    }

    public static String useCatalog(String catalog) {
        return String.format("USE CATALOG %s", catalog);
    }

    public static String showDatabases() {
        return "SHOW DATABASES";
    }

    public static String useDatabase(String database) {
        return String.format("USE %s", database);
    }

    public static String showTables() {
        return "SHOW TABLES";
    }

    public static String showViews() {
        return "SHOW VIEWS";
    }

    public static String showFunctions() {
        return "SHOW FUNCTIONS";
    }

    public static String showUserFunctions() {
        return "SHOW USER FUNCTIONS";
    }

    public static String showModules() {
        return "SHOW MODULES";
    }

    public static String descTable(String table) {
        return String.format("DESC %s", table);
    }

    public static String columnName() {
        return "name";
    }

    public static String columnType() {
        return "type";
    }

    public static String columnNull() {
        return "null";
    }

    public static String columnKey() {
        return "key";
    }

    public static String columnExtras() {
        return "extras";
    }

    public static String columnWatermark() {
        return "watermark";
    }
}
