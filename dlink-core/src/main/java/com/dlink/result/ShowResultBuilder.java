package com.dlink.result;

import com.dlink.utils.FlinkUtil;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;

import java.util.*;

/**
 * ShowResultBuilder
 *
 * @author wenmo
 * @since 2021/7/1 23:57
 */
public class ShowResultBuilder implements ResultBuilder {

    private String nullColumn = "";

    public ShowResultBuilder() {
    }

    @Override
    public IResult getResult(TableResult tableResult) {
        List<String> columns = FlinkUtil.catchColumn(tableResult);
        Set<String> column = new LinkedHashSet(columns);
        List<Map<String, Object>> rows = new ArrayList<>();
        Iterator<Row> it = tableResult.collect();
        while (it.hasNext()) {
            Map<String, Object> map = new LinkedHashMap<>();
            Row row = it.next();
            for (int i = 0; i < row.getArity(); ++i) {
                Object field = row.getField(i);
                if (field == null) {
                    map.put(columns.get(i), nullColumn);
                } else {
                    map.put(columns.get(i), field.toString());
                }
            }
            rows.add(map);
        }
        return new DDLResult(rows, rows.size(), column);
    }
}
