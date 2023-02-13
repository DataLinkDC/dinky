package org.dinky.executor;

import com.google.common.collect.Multimaps;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class ExecutorSettingTest {

    @Test
    void build() {
        Map<String, String> maps = new HashMap<>();
        maps.put("123", "123");
        maps.put("456", null);
        assertEquals(123, (int) NumberUtils.createInteger(maps.get("123")));
        assertNull(NumberUtils.createInteger(maps.get("456")));
        assertNull(NumberUtils.createInteger(maps.get("789")));
    }
}
