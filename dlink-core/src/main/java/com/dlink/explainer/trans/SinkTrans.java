package com.dlink.explainer.trans;


import com.dlink.utils.MapParseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SinkTrans
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class SinkTrans extends AbstractTrans implements Trans {

    private String catalog;
    private String database;
    private String table;
    private List<String> fields;

    public final static String TRANS_TYPE = "Data Sink";

    public SinkTrans() {
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    @Override
    public String getHandle() {
        return TRANS_TYPE;
    }

    @Override
    public boolean canHandle(String pact) {
        return TRANS_TYPE.equals(pact);
    }

    @Override
    public void translate() {
        Map map = MapParseUtils.parse(contents);
        ArrayList<String> tables = (ArrayList<String>) map.get("table");
        if(tables!=null&&tables.size()>0) {
            name = tables.get(0);
            String [] names = tables.get(0).split("\\.");
            if (names.length >= 3) {
                catalog = names[0];
                database = names[1];
                table = names[2];
            } else {
                table = name;
            }
        }
        fields = (ArrayList<String>) map.get("fields");
    }

    @Override
    public String asSummaryString() {
        return "";
    }

}
