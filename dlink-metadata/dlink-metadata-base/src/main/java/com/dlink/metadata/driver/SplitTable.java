package com.dlink.metadata.driver;

import com.dlink.model.Table;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ZackYoung
 * @version 1.0
 * @date 2022/8/19
 */
public interface SplitTable {

    Set<Table> getSplitTables(List<String> tableRegList, Map<String, String> split);
    List<Map<String, String>> getSplitSchemaList();
}
