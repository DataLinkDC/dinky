package com.dlink.explainer.lineage;

import com.dlink.assertion.Asserts;
import com.dlink.explainer.ca.TableCA;
import com.dlink.explainer.trans.OperatorTrans;
import com.dlink.explainer.trans.SinkTrans;
import com.dlink.explainer.trans.SourceTrans;
import com.dlink.explainer.trans.Trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LineageTableGenerator
 *
 * @author wenmo
 * @since 2022/3/16 19:56
 **/
public class LineageTableGenerator {

    private Map<Integer, Trans> transMaps;
    private List<TableCA> tables = new ArrayList<>();

    public LineageTableGenerator() {
    }

    public static LineageTableGenerator build(List<Trans> transList) {
        LineageTableGenerator generator = new LineageTableGenerator();
        Map<Integer, Trans> map = new HashMap<>();
        for (Trans trans : transList) {
            map.put(trans.getId(), trans);
        }
        generator.setTransMaps(map);
        return generator;
    }

    public void translate() {
        for (Map.Entry<Integer, Trans> entry : transMaps.entrySet()) {
            if (entry.getValue() instanceof SourceTrans) {
                tables.add(TableCA.build(entry.getValue()));
            } else if (entry.getValue() instanceof SinkTrans) {
                tables.add(TableCA.build(entry.getValue()));
            } else if (entry.getValue() instanceof OperatorTrans) {
                TableCA tableCA = TableCA.build(entry.getValue());
                if (Asserts.isNotNull(tableCA)) {
                    tables.add(tableCA);
                }
            }
        }
    }

    public Map<Integer, Trans> getTransMaps() {
        return transMaps;
    }

    public void setTransMaps(Map<Integer, Trans> transMaps) {
        this.transMaps = transMaps;
    }

    public List<TableCA> getTables() {
        return tables;
    }

    public void setTables(List<TableCA> tables) {
        this.tables = tables;
    }
}
