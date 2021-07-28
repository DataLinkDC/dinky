package com.dlink.metadata.driver;

import com.dlink.assertion.Asserts;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.convert.OracleTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.OracleQuery;
import com.dlink.model.Column;
import com.dlink.model.Table;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OracleDriver
 *
 * @author wenmo
 * @since 2021/7/21 15:52
 **/
public class OracleDriver extends AbstractJdbcDriver {

    @Override
    String getDriverClass() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public IDBQuery getDBQuery() {
        return new OracleQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new OracleTypeConvert();
    }

    @Override
    public String getType() {
        return "Oracle";
    }

    @Override
    public String getName() {
        return "Oracle数据库";
    }

    @Override
    public String getCreateTableSql(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(table.getName() + " (");
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if(i>0){
                sb.append(",");
            }
            sb.append(columns.get(i).getName() + " " + getTypeConvert().convertToDB(columns.get(i).getType()));
            if("YES".equals(columns.get(i).getIsNotNull())){
                sb.append(" NOT NULL");
            }
        }
        sb.append(");");
        sb.append("\r\n");
        List<Column> pks = columns.stream().filter(column -> column.isKeyFlag()).collect(Collectors.toList());
        if(Asserts.isNotNullCollection(pks)) {
            sb.append("ALTER TABLE " + table.getName() + " ADD CONSTRAINT " + table.getName() + "_PK PRIMARY KEY (");
            for (int i = 0; i < pks.size(); i++) {
                if(i>0){
                    sb.append(",");
                }
                sb.append(pks.get(i).getName());
            }
            sb.append(");\r\n");
        }
        for (int i = 0; i < columns.size(); i++) {
            sb.append("COMMENT ON COLUMN "+table.getName()+"."+columns.get(i).getName()+" IS '"+columns.get(i).getComment()+"';");
        }
        return sb.toString();
    }

}
