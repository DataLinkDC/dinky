package org.dinky.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SqlTypeTest {

    @Test
    public void match() {
        test("CREATE TABLE Orders (\n" +
                "    order_number BIGINT,\n" +
                "    price        DECIMAL(32,2),\n" +
                "    buyer        ROW<first_name STRING, last_name STRING>,\n" +
                "    order_time   TIMESTAMP(3)\n" +
                ") WITH (\n" +
                "  'connector' = 'datagen',\n" +
                "  'rows-per-second' = '1'\n" +
                ");", SqlType.CREATE, true);
        test("select * from Orders", SqlType.SELECT, true);
        test("DROP...", SqlType.DROP, true);
        test("ALTER...", SqlType.ALTER, true);
        test("INSERT...", SqlType.INSERT, true);
        test("DESC...", SqlType.DESC, true);
        test("DESCRIBE...", SqlType.DESCRIBE, true);
        test("EXPLAIN...", SqlType.EXPLAIN, true);
        test("USE...", SqlType.USE, true);
        test("SHOW...", SqlType.SHOW, true);
        test("LOAD...", SqlType.LOAD, true);
        test("UNLOAD...", SqlType.UNLOAD, true);
        test("SET...", SqlType.SET, true);
        test("RESET...", SqlType.RESET, true);
        test("EXECUTE...", SqlType.EXECUTE, true);
        test("ADD...", SqlType.ADD, true);
        test("WATCH...", SqlType.WATCH, true);

        String sql = "CREATE TABLE print_OrdersView WITH ('connector' = 'printnet', 'port'='7125', 'hostName' = '172" +
                ".26.16.1', 'sink.parallelism'='1')\n" +
                "AS SELECT * FROM OrdersView; ";
        test(sql, SqlType.CTAS, true);
        test(sql, SqlType.CREATE, false);
    }

    private void test(String sql, SqlType sqlType, boolean result) {
        Assertions.assertThat(sqlType.match(sql)).isEqualTo(result);
    }
}
