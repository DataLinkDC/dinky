/*

#     __                        
#    /  |  ____ ___  _          
#   / / | / __//   // / /       
#  /_/`_|/_/  / /_//___/        
create @ 2022/7/9                                
*/
package com.dlink.flink.catalog;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.junit.Before;
import org.junit.Test;

import static org.apache.flink.table.api.config.ExecutionConfigOptions.TABLE_EXEC_RESOURCE_DEFAULT_PARALLELISM;

public class DlinkMysqlCatalogTest {

    protected static String url;
    protected static DlinkMysqlCatalog catalog;

    protected static final String TEST_CATALOG_NAME = "mysql-catalog";
    protected static final String TEST_USERNAME = "flink_metastore";
    protected static final String TEST_PWD = "flink_metastore";

    private TableEnvironment tableEnv;

    @Before
    public void setup(){

        url = "jdbc:mysql://localhost:3306/flink_metastore?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";

        catalog =
                new DlinkMysqlCatalog(
                        TEST_CATALOG_NAME,
                        url,
                        TEST_USERNAME,
                        TEST_PWD);

        this.tableEnv = TableEnvironment.create(EnvironmentSettings.inStreamingMode());
        tableEnv.getConfig()
                .getConfiguration()
                .setInteger(TABLE_EXEC_RESOURCE_DEFAULT_PARALLELISM.key(), 1);

    }

    @Test
    public void testSqlCatalog(){
        String createSql = "create catalog myCatalog \n" +
                " with('type'='dlink_mysql_catalog',\n" +
                " 'mysql-catalog-username'='flink_metastore',\n" +
                " 'mysql-catalog-password'='flink_metastore',\n" +
                " 'mysql-catalog-url'='jdbc:mysql://localhost:3306/" +
                "flink_metastore?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC')";
        tableEnv.executeSql(createSql);
        tableEnv.executeSql("use catalog myCatalog");
    }

    @Test
    public void test() {
        // 这个 test 依赖个人环境，直接保留会导致打包不通过。但是展示了用法。
        /*//1\. 获取上下文环境 table的环境

        // use mysql-catalog
        tableEnv.registerCatalog(DlinkMysqlCatalogFactoryOptions.IDENTIFIER, catalog);
        tableEnv.useCatalog(DlinkMysqlCatalogFactoryOptions.IDENTIFIER);

        //2\. 读取score.csv

        String csvFile = "D:\\code\\test\\mycatalog\\src\\test\\resources\\score.csv";
        String createTable = "CREATE TABLE IF NOT EXISTS player_data\n" +
                "( season varchar,\n" +
                "  player varchar,\n" +
                "  play_num varchar,\n" +
                "  first_court int,\n" +
                "  `time` double,\n" +
                "  assists double,\n" +
                "  steals double,\n" +
                "  blocks double,\n" +
                "  scores double\n" +
                ") WITH ( \n" +
                "    'connector' = 'filesystem',\n" +
                "    'path' = '" + csvFile + " ',\n" +
                "    'format' = 'csv'\n" +
                ")";

        tableEnv.executeSql(createTable);

        String createView= "CREATE VIEW IF NOT EXISTS test_view " +
                " (player, play_num" +
                " ,sumaabb)" +
                " COMMENT 'test view' " +
                " AS SELECT player, play_num, assists + steals as sumaabb FROM player_data";

        tableEnv.executeSql(createView);

        String createSinkTable = "CREATE TABLE IF NOT EXISTS mysql_player_from_view\n" +
                "( " +
                "  player varchar,\n" +
                "  play_num varchar,\n" +
                "  sumaabb double\n" +
                ") WITH ( \n" +
                "    'connector' = 'jdbc',\n" +
                "    'url' = 'jdbc:mysql://localhost:3306/a01_rep_db',\n" +
                "    'table-name' = 'mysql_player_from_view',\n" +
                "    'username' = 'root',\n" +
                "    'password' = '123456'\n" +
                ")";



        tableEnv.executeSql(createSinkTable);
        tableEnv.executeSql("Insert into mysql_player_from_view\n" +
                "SELECT \n" +
                "player ,\n" +
                "  play_num ,\n" +
                "  sumaabb \n" +
                "FROM test_view");

        List<Row> results =
                CollectionUtil.iteratorToList(
                        tableEnv.sqlQuery("select * from mysql_player_from_view")
                                .execute()
                                .collect());

        List<Row> tresults =
                CollectionUtil.iteratorToList(
                        tableEnv.sqlQuery("select * from test_view")
                                .execute()
                                .collect());

        List<Row> presults =
                CollectionUtil.iteratorToList(
                        tableEnv.sqlQuery("select * from player_data")
                                .execute()
                                .collect());
         */
    }
}
