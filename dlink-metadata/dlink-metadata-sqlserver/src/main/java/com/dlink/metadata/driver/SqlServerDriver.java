package com.dlink.metadata.driver;

import com.dlink.metadata.constant.SqlServerConstant;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.convert.SqlServerTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.SqlServerQuery;
import com.dlink.model.Column;
import com.dlink.model.Table;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lcg
 * @operate 
 * @date 2022/1/26 14:23
 * @return 
 */
public class SqlServerDriver  extends AbstractJdbcDriver {
    @Override
    public IDBQuery getDBQuery() {
        return new SqlServerQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new SqlServerTypeConvert();
    }

    @Override
    String getDriverClass() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public String getType() {
        return "SqlServer";
    }

    @Override
    public String getName() {
        return "SqlServer数据库";
    }

    @Override
    public String getCreateTableSql(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ["+table.getName() + "] (");
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if(i>0){
                sb.append(",");
            }
            sb.append("["+columns.get(i).getName() + "]" + getTypeConvert().convertToDB(columns.get(i)));
            if(columns.get(i).isNullable()){
                sb.append(" NOT NULL");
            }else{
                sb.append(" NULL");
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
                sb.append("["+pks.get(i)+"]");
            }
            sb.append(" ) ");
        }
        sb.append(") GO ");
        for (Column column : columns) {
            String comment = column.getComment();
            if(comment != null && !comment.isEmpty()){
                sb.append(String.format(SqlServerConstant.COMMENT_SQL, comment, table.getSchema() == null || table.getSchema().isEmpty() ? "dbo":table.getSchema(),
                        table.getName(), column.getName()) + " GO ");
            }
        }
        return sb.toString();
    }
}
