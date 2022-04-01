package com.dlink.explainer.lineage;

import com.dlink.explainer.ca.ColumnCAResult;
import com.dlink.explainer.ca.NodeRel;
import com.dlink.explainer.ca.TableCA;
import com.dlink.plus.FlinkSqlPlus;

import java.util.*;

/**
 * LineageBuilder
 *
 * @author wenmo
 * @since 2022/3/15 22:58
 */
public class LineageBuilder {

    public static LineageResult getLineage(String statement) {
        return getLineage(statement, true);
    }

    public static LineageResult getLineage(String statement, boolean statementSet) {
        FlinkSqlPlus plus = FlinkSqlPlus.build(statementSet);
        List<ColumnCAResult> columnCAResults = plus.explainSqlColumnCA(statement);
        List<LineageTable> tables = new ArrayList<>();
        List<LineageRelation> relations = new ArrayList<>();
        int index = 0;
        for (ColumnCAResult item : columnCAResults) {
            for (TableCA tableCA : item.getTableCAS()) {
                tables.add(LineageTable.build(tableCA));
            }
            Set<String> keySet = new HashSet<>();
            for (NodeRel nodeRel : item.getColumnCASRelChain()) {
                if( item.getColumnCASMaps().containsKey(nodeRel.getPreId())&&item.getColumnCASMaps().containsKey(nodeRel.getSufId())
                && !item.getColumnCASMaps().get(nodeRel.getPreId()).getTableId().equals(item.getColumnCASMaps().get(nodeRel.getSufId()).getTableId())) {
                    String key = item.getColumnCASMaps().get(nodeRel.getPreId()).getTableId().toString() + "@" +
                            item.getColumnCASMaps().get(nodeRel.getSufId()).getTableId().toString() + "@" +
                            item.getColumnCASMaps().get(nodeRel.getPreId()).getName() + "@" +
                            item.getColumnCASMaps().get(nodeRel.getSufId()).getName();
                    //去重
                    if(!keySet.contains(key)){
                        index++;
                        relations.add(LineageRelation.build(index + "",
                                item.getColumnCASMaps().get(nodeRel.getPreId()).getTableId().toString(),
                                item.getColumnCASMaps().get(nodeRel.getSufId()).getTableId().toString(),
                                item.getColumnCASMaps().get(nodeRel.getPreId()).getName(),
                                item.getColumnCASMaps().get(nodeRel.getSufId()).getName()
                        ));
                        keySet.add(key);
                    }
                }
            }
        }
        //获取重复表集合
        List<List<LineageTable>> repeatTablesList = new ArrayList<>();
        for (int i = 0; i < tables.size() - 1; i++) {
            List<LineageTable> repeatTables = new ArrayList<>();
            for (int j = i + 1; j < tables.size(); j++) {
                if (tables.get(i).getName().equals(tables.get(j).getName())) {
                    repeatTables.add(tables.get(j));
                }
            }
            if (repeatTables.size() > 0) {
                repeatTables.add(tables.get(i));
                repeatTablesList.add(repeatTables);
            }
        }
        //重复表合并
        Map<String,String> correctTableIdMap = new HashMap<>();
        for(List<LineageTable> tableList : repeatTablesList){
            LineageTable newTable = new LineageTable();
            Set<String> columnKeySet = new HashSet<>();
            for(LineageTable table: tableList){
                if(newTable.getId() == null || newTable.getName() == null){
                    newTable.setId(table.getId());
                    newTable.setName(table.getName());
                    newTable.setColumns(new ArrayList<>());
                }
                for(LineageColumn column : table.getColumns()){
                    String key = column.getName() + "@&" + column.getTitle();
                    if(!columnKeySet.contains(key)){
                        newTable.getColumns().add(column);
                        columnKeySet.add(key);
                    }
                }
                correctTableIdMap.put(table.getId(),newTable.getId());
                tables.remove(table);
            }
            tables.add(newTable);
        }
        //关系中id重新指向
        for (LineageRelation relation : relations){
            if(correctTableIdMap.containsKey(relation.getSrcTableId())){
                relation.setSrcTableId(correctTableIdMap.get(relation.getSrcTableId()));
            }
            if(correctTableIdMap.containsKey(relation.getTgtTableId())){
                relation.setTgtTableId(correctTableIdMap.get(relation.getTgtTableId()));
            }
        }
        return LineageResult.build(tables, relations);
    }
}
