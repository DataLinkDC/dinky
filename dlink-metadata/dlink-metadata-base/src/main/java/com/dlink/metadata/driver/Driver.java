package com.dlink.metadata.driver;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

import com.dlink.assertion.Asserts;
import com.dlink.exception.MetaDataException;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import com.dlink.result.SqlExplainResult;

/**
 * Driver
 *
 * @author wenmo
 * @since 2021/7/19 23:15
 */
public interface Driver {

    static Optional<Driver> get(DriverConfig config) {
        Asserts.checkNotNull(config, "数据源配置不能为空");
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        for (Driver driver : drivers) {
            if (driver.canHandle(config.getType())) {
                return Optional.of(driver.setDriverConfig(config));
            }
        }
        return Optional.empty();
    }

    static Driver build(DriverConfig config) {
        String key = config.getName();
        if (DriverPool.exist(key)) {
            Driver driver = DriverPool.get(key);
            if (driver.isHealth()) {
                return driver;
            }
        }
        Optional<Driver> optionalDriver = Driver.get(config);
        if (!optionalDriver.isPresent()) {
            throw new MetaDataException("不支持数据源类型【" + config.getType() + "】，请在 lib 下添加扩展依赖");
        }
        Driver driver = optionalDriver.get().connect();
        DriverPool.push(key, driver);
        return driver;
    }

    Driver setDriverConfig(DriverConfig config);

    boolean canHandle(String type);

    String getType();

    String getName();

    String test();

    boolean isHealth();

    Driver connect();

    void close();

    List<Schema> listSchemas();

    List<Table> listTables(String schemaName);

    List<Column> listColumns(String schemaName, String tableName);

    List<Column> listColumnsSortByPK(String schemaName, String tableName);

    List<Column> listColumnsSortByCustom(String schemaName, String tableName, List<String> customSort);

    List<Schema> getSchemasAndTables();

    List<Table> getTablesAndColumns(String schemaName);

    Table getTable(String schemaName, String tableName);

    boolean existTable(Table table);

    boolean createTable(Table table) throws Exception;

    boolean dropTable(Table table) throws Exception;

    boolean truncateTable(Table table) throws Exception;

    String getCreateTableSql(Table table);

    String getDropTableSql(Table table);

    String getTruncateTableSql(Table table);

   /* boolean insert(Table table, JsonNode data);

    boolean update(Table table, JsonNode data);

    boolean delete(Table table, JsonNode data);

    SelectResult select(String sql);*/

    boolean execute(String sql) throws Exception;

    int executeUpdate(String sql) throws Exception;

    JdbcSelectResult query(String sql, Integer limit);

    JdbcSelectResult executeSql(String sql, Integer limit);

    List<SqlExplainResult> explain(String sql);

    Map<String, String> getFlinkColumnTypeConversion();
}
