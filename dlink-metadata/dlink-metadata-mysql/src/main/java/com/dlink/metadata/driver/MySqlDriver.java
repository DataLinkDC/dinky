package com.dlink.metadata.driver;

import com.dlink.assertion.Asserts;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.convert.MySqlTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.MySqlQuery;
import com.dlink.metadata.result.SelectResult;
import com.dlink.model.Column;
import com.dlink.model.Table;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * MysqlDriver
 *
 * @author wenmo
 * @since 2021/7/20 14:06
 **/
public class MySqlDriver extends AbstractJdbcDriver {

    @Override
    public IDBQuery getDBQuery() {
        return new MySqlQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new MySqlTypeConvert();
    }

    @Override
    public String getType() {
        return "MySql";
    }

    @Override
    public String getName() {
        return "MySql数据库";
    }

    @Override
    public String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public String getCreateTableSql(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE `"+table.getName() + "` (");
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if(i>0){
                sb.append(",");
            }
            sb.append("`"+columns.get(i).getName() + "` " + getTypeConvert().convertToDB(columns.get(i)));
            if("YES".equals(columns.get(i).getIsNotNull())){
                sb.append(" NOT NULL");
            }else{
                sb.append(" NULL");
            }
            if(Asserts.isNotNull(columns.get(i).getComment())){
                sb.append(" COMMENT '"+columns.get(i).getComment()+"'");
            }
        }
        List<String> pks = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if(columns.get(i).isKeyFlag()){
                pks.add(columns.get(i).getName());
            }
        }
        if(pks.size()>0){
            sb.append(", PRIMARY KEY ( ");
            for (int i = 0; i < pks.size(); i++) {
                if(i>0){
                    sb.append(",");
                }
                sb.append("`"+pks.get(i)+"`");
            }
            sb.append(" ) USING BTREE");
        }
        sb.append(") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '"+(table.getComment()!=null?table.getComment():"")+"' ROW_FORMAT = Dynamic;");
        return sb.toString();
    }

}
