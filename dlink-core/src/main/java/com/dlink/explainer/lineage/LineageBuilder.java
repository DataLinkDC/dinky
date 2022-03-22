package com.dlink.explainer.lineage;

import com.dlink.explainer.ca.ColumnCAResult;
import com.dlink.explainer.ca.NodeRel;
import com.dlink.explainer.ca.TableCA;
import com.dlink.plus.FlinkSqlPlus;

import java.util.ArrayList;
import java.util.List;

/**
 * LineageBuilder
 *
 * @author wenmo
 * @since 2022/3/15 22:58
 */
public class LineageBuilder {

    public static LineageResult getLineage(String statement) {
        FlinkSqlPlus plus = FlinkSqlPlus.build();
        List<ColumnCAResult> columnCAResults = plus.explainSqlColumnCA(statement);
        List<LineageTable> tables = new ArrayList<>();
        List<LineageRelation> relations = new ArrayList<>();
        int index = 0;
        for (ColumnCAResult item : columnCAResults) {
            for (TableCA tableCA : item.getTableCAS()) {
                tables.add(LineageTable.build(tableCA));
            }
            for (NodeRel nodeRel : item.getColumnCASRelChain()) {
                index++;
                relations.add(LineageRelation.build(index + "",
                        item.getColumnCASMaps().get(nodeRel.getPreId()).getTableId().toString(),
                        item.getColumnCASMaps().get(nodeRel.getSufId()).getTableId().toString(),
                        item.getColumnCASMaps().get(nodeRel.getPreId()).getName(),
                        item.getColumnCASMaps().get(nodeRel.getSufId()).getName()
                ));
            }
        }
        return LineageResult.build(tables, relations);
    }
}
