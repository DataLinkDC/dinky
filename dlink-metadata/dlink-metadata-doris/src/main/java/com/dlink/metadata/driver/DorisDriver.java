package com.dlink.metadata.driver;

import com.dlink.assertion.Asserts;
import com.dlink.metadata.convert.DorisTypeConvert;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.query.DorisQuery;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.model.Column;
import com.dlink.model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DorisDriver extends  AbstractJdbcDriver{
    @Override
    public IDBQuery getDBQuery() {
        return new DorisQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new DorisTypeConvert();
    }

    @Override
    String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public String getType() {
        return "Doris";
    }

    @Override
    public String getName() {
        return "Doris";
    }

    @Override
    public String getCreateTableSql(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + table.getSchema() + "." + table.getName() + " (");
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append( columns.get(i).getName() + " " + getTypeConvert().convertToDB(columns.get(i)));
            if ("YES".equals(columns.get(i).isNullable())) {
                sb.append(" NOT NULL ");
            } else {
                sb.append(" NULL ");
            }
            if (Asserts.isNotNullString(columns.get(i).getComment())) {
                sb.append(" COMMENT '" + columns.get(i).getComment() + "' ");
            }
        }
        sb.append(" ) ENGINE = olap ");
        List<String> pks = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isKeyFlag()) {
                pks.add(columns.get(i).getName());
            }
        }
        if (pks.size() > 0) {
            sb.append("UNIQUE KEY( ");
            for (int i = 0; i < pks.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(pks.get(i));
            }
            sb.append(") ");
        }
        if(Asserts.isNotNullString(table.getComment())){
            sb.append("COMMENT '" + table.getComment() + "' ");
        }
        sb.append("DISTRIBUTED BY HASH(" + pks.get(0) + ") BUCKETS 32 ");
        sb.append("PROPERTIES('replication_num' = '1')");
        return sb.toString();
    }

    @Override
    public Map<String,String> getFlinkColumnTypeConversion(){
        return new HashMap<>();
    }
}
