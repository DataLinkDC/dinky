package com.dlink.explainer.ca;

import com.dlink.plus.FlinkSqlPlus;

import java.util.ArrayList;
import java.util.List;

/**
 * CABuilder
 *
 * @author qiwenkai
 * @since 2021/6/23 11:03
 **/
public class CABuilder {

    public static List<TableCANode> getOneTableCAByStatement(String statement){
        List<TableCANode> tableCANodes = new ArrayList<>();
        FlinkSqlPlus plus = FlinkSqlPlus.build();
        List<TableCAResult> results = plus.explainSqlTableCA(statement);
        for (int j = 0; j < results.size(); j++) {
            TableCAResult result = results.get(j);
            TableCANode node = new TableCANode();
            TableCA sinkTableCA = (TableCA)result.getSinkTableCA();
            node.setName(sinkTableCA.getTableName());
            List<TableCANode> children = new ArrayList<>();
            for (int k = 0; k < result.getSourceTableCAS().size(); k++) {
                children.add(new TableCANode(result.getSourceTableCAS().get(k).getTableName()));
            }
            node.setChildren(children);
            tableCANodes.add(node);
        }
        return tableCANodes;
    }

    public static List<TableCANode> getOneTableColumnCAByStatement(String statement){
        List<TableCANode> tableCANodes = new ArrayList<>();
        FlinkSqlPlus plus = FlinkSqlPlus.build();
        int id=1;
        List<TableCAResult> results = plus.explainSqlTableColumnCA(statement);
        for (int j = 0; j < results.size(); j++) {
            TableCAResult result = results.get(j);
            TableCA sinkTableCA = (TableCA)result.getSinkTableCA();
            TableCANode node = new TableCANode(id++,sinkTableCA.getTableName(),sinkTableCA.getFields());
            List<TableCANode> children = new ArrayList<>();
            for (int k = 0; k < result.getSourceTableCAS().size(); k++) {
                TableCA tableCA = (TableCA) result.getSourceTableCAS().get(k);
                children.add(new TableCANode(id++,tableCA.getTableName(),tableCA.getFields()));
            }
            node.setChildren(children);
            tableCANodes.add(node);
        }
        return tableCANodes;
    }
}
