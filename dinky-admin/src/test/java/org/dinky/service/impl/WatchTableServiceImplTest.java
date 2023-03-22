package org.dinky.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WatchTableServiceImplTest {

    @Test
    void getDestination() {
        String tableName = "`default_catalog`.`default_database`.`Orders`";
        String result = WatchTableServiceImpl.getDestination(123, tableName);
        assertEquals("/topic/table/123/`default_catalog`.`default_database`.`print_Orders`", result);

        result = WatchTableServiceImpl.getDestination(123, "Orders");
        assertEquals("/topic/table/123/`default_catalog`.`default_database`.`print_Orders`", result);
    }
}
