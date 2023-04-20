package org.zdpx.coder.utils;


import org.zdpx.coder.operator.Column;
import org.zdpx.coder.operator.TableInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class TableDataStreamConverter {
    private TableDataStreamConverter() {
    }

    @SuppressWarnings("unchecked")
    public static TableInfo getTableInfo(Map<String, Object> dataModel) {
        List<Map<String, String>> columns = (List<Map<String, String>>) dataModel.get("columns");
        List<Column> cs = new ArrayList<>();
        for (Map<String, String> dm : columns) {
            cs.add(new Column(dm.get("name"), dm.get("type")));
        }

        return TableInfo.newBuilder()
                .name((String)dataModel.get("tableName"))
                .columns(cs)
                .build();
    }
}
