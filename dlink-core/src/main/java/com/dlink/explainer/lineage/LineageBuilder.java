package com.dlink.explainer.lineage;

import com.dlink.explainer.ca.ColumnCAResult;
import com.dlink.plus.FlinkSqlPlus;

import java.util.List;

/**
 * LineageBuilder
 *
 * @author wenmo
 * @since 2022/3/15 22:58
 */
public class LineageBuilder {

    public static LineageResult getLineage(String statement){
        FlinkSqlPlus plus = FlinkSqlPlus.build();
        List<ColumnCAResult> columnCAResults = plus.explainSqlColumnCA(statement);
        for (int j = 0; j < columnCAResults.size(); j++) {
            ColumnCAResult result = columnCAResults.get(j);
        }
        return null;
    }
}
