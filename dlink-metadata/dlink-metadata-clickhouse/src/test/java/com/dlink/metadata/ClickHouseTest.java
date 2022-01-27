package com.dlink.metadata;

import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import org.junit.Test;

import java.util.List;

public class ClickHouseTest {

  private static final String IP = "slave02";

  public Driver getDriver() {
    DriverConfig config = new DriverConfig();
    config.setType("ClickHouse");
    config.setIp(IP);
    config.setPort(8123);
    config.setUsername("default");
    config.setPassword("1q2w3e4r5T");
    config.setUrl("jdbc:clickhouse://" + IP + ":8123/test");
    return Driver.build(config).connect();
  }

  @Test
  public void connectTest() {
    String test = getDriver().test();
    System.out.println(test);
    System.out.println("end...");
  }

  @Test
  public void schemaTest() {
    Driver driver = getDriver();
    List<Schema> schemasAndTables = driver.getSchemasAndTables();
    for (int i = 0; i < schemasAndTables.size(); i++) {
      Schema db = schemasAndTables.get(i);
      for (Table tbl : db.getTables()) {
        System.out.println(String.format("%s.%s", db.getName(), tbl.getName()));
      }
    }

    System.out.println("end...");
  }

  @Test
  public void columnTest() {
    Driver driver = getDriver();
    List<Column> columns = driver.listColumns("test", "source_demo");
    System.out.println(
        "name,type,comment,keyFlag,autoIncrement,defaultValue,isNullable,javaType,columnFamily,position,precision,scale,characterSet,collation");
    for (Column c : columns) {
      System.out.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
          c.getName(), c.getType(), c.getComment(), c.isKeyFlag(), c.isAutoIncrement(),
          c.getDefaultValue(), c.isNullable(), c.getJavaType(), c.getColumnFamily(),
          c.getPosition(), c.getPrecision(), c.getScale(), c.getCharacterSet(), c.getCollation()
      ));
    }
    System.out.println("end...");
  }

  @Test
  public void queryTest() {
    Driver driver = getDriver();
    JdbcSelectResult query = driver.query("select * from test.source_demo", 10);
    System.out.println(query.getColumns());
    System.out.println("end...");
  }
}
