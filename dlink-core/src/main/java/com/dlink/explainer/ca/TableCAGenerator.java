package com.dlink.explainer.ca;

import com.dlink.assertion.Asserts;
import com.dlink.explainer.trans.Field;
import com.dlink.explainer.trans.OperatorTrans;
import com.dlink.explainer.trans.SinkTrans;
import com.dlink.explainer.trans.SourceTrans;
import com.dlink.explainer.trans.Trans;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TableCAGenerator
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class TableCAGenerator implements CAGenerator {

    private List<Trans> transList;
    private Map<Integer, Trans> transMaps;
    private Set<Integer> parentIdSet;
    private List<ICA> sourceTableCAS = new ArrayList<>();
    private ICA sinkTableCA = null;
    private String sinkTableName;

    public TableCAGenerator(List<Trans> transList) {
        this.transList = transList;
        this.transMaps = new HashMap<>();
        this.parentIdSet = new HashSet<>();
        for (int i = 0; i < transList.size(); i++) {
            this.transMaps.put(transList.get(i).getId(),transList.get(i));
            if(transList.get(i).getParentId()!=null) {
                parentIdSet.add(transList.get(i).getParentId());
            }
        }
    }

    public static TableCAGenerator build(List<Trans> transList) {
        return new TableCAGenerator(transList);
    }

    public TableCAResult getResult(){
        return new TableCAResult(this);
    }

    @Override
    public void translate() {
        for(Trans trans : transList){
            if(trans instanceof SourceTrans) {
                TableCA tableCA = TableCA.build(trans);
                List<String> sourceFields = new ArrayList<>();
                CollectionUtils.addAll(sourceFields, new Object[tableCA.getFields().size()]);
                Collections.copy(sourceFields, tableCA.getFields());
                for (int j = 0; j < sourceFields.size(); j++) {
                    buildTableCAFields(tableCA,tableCA.getParentId(),sourceFields.get(j));
                }
                this.sourceTableCAS.add(tableCA);
            }else if(trans instanceof SinkTrans) {
                TableCA tableCA = TableCA.build(trans);
                this.sinkTableCA = tableCA;
                this.sinkTableName = tableCA.getName();
            }
        }
    }

    public void translateOnlyTable() {
        for(Trans trans : transList){
            if(trans instanceof SourceTrans) {
                this.sourceTableCAS.add(new TableCA((SourceTrans) trans));
            }else if(trans instanceof SinkTrans) {
                TableCA tableCA = new TableCA((SinkTrans) trans);
                this.sinkTableCA = tableCA;
                this.sinkTableName = tableCA.getName();
            }
        }
    }

    private void buildTableCAFields(TableCA tableCA,Integer id,String field){
        if(transMaps.get(id) instanceof OperatorTrans){
            OperatorTrans trans = (OperatorTrans) transMaps.get(id);
            searchSelectFields(tableCA, trans.getSelect(),field);
            searchWhereFields(tableCA, trans.getWhere(),field);
            if(Asserts.isNotNull(trans.getParentId())){
                buildTableCAFields(tableCA,trans.getParentId(),field);
            }
        }
    }

    private void searchSelectFields(TableCA tableCA, List<Field> selects, String field){
        if(Asserts.isNotNull(selects)){
            for (int i = 0; i < selects.size(); i++){
                List<String> fields = matchFields( selects.get(i).getFragment(),field);
                /*if(tableCA.getFields().contains(field)){
                    tableCA.getFields().remove(field);
                }*/
                for (int j = 0; j < fields.size(); j++) {
                    if(!tableCA.getUseFields().contains(fields.get(j))){
                        tableCA.getUseFields().add(fields.get(j));
                    }
                }
            }
        }
    }

    private void searchWhereFields(TableCA tableCA,String wheres,String field){
        if(Asserts.isNotNull(wheres)&&!"[]".equals(wheres)){
            List<String> fields = matchFields( wheres,field);
            /*if(tableCA.getFields().contains(field)){
                tableCA.getFields().remove(field);
            }*/
            for (int j = 0; j < fields.size(); j++) {
                if(!tableCA.getUseFields().contains(fields.get(j))){
                    tableCA.getUseFields().add(fields.get(j));
                }
            }
        }
    }

    private List<String> matchFields(String fragement,String field){
        List<String> fields = new ArrayList<>();
        Pattern p = Pattern.compile(field+"\\.(.*?) ");
        Matcher m = p.matcher(fragement+" ");
        while(m.find()){
            fields.add(m.group(0).replaceFirst("\\)","").trim());
        }
        if(fragement.equals(field)){
            fields.add(fragement.trim());
        }
        return fields;
    }

    public List<Trans> getTransList() {
        return transList;
    }

    public Map<Integer, Trans> getTransMaps() {
        return transMaps;
    }

    public Set<Integer> getParentIdSet() {
        return parentIdSet;
    }

    public List<ICA> getSourceTableCAS() {
        return sourceTableCAS;
    }

    public ICA getSinkTableCA() {
        return sinkTableCA;
    }

    public String getSinkTableName() {
        return sinkTableName;
    }
}
