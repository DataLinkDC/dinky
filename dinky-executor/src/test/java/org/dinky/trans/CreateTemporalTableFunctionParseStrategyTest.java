package org.dinky.trans;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CreateTemporalTableFunctionParseStrategyTest {

    @Test
    void getInfo() {
        String statement = "create temporal temporary function func as select price, buyer from orders";
        String[] result = CreateTemporalTableFunctionParseStrategy.getInfo(statement);
        Assertions.assertArrayEquals(new String[] {"TEMPORARY", "", "FUNC", "price", "buyer", "orders"}, result);
    }
}
