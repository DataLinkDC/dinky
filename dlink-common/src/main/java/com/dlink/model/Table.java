package com.dlink.model;

import com.dlink.assertion.Asserts;
import com.dlink.utils.SqlUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Table
 *
 * @author wenmo
 * @since 2021/7/19 23:27
 */
@Getter
@Setter
public class Table implements Serializable, Comparable<Table> {

    private static final long serialVersionUID = 4209205512472367171L;

    private String name;
    private String schema;
    private String catalog;
    private String comment;
    private String type;
    private String engine;
    private String options;
    private Long rows;
    private Date createTime;
    private Date updateTime;
    private List<Column> columns;

    public Table() {
    }

    public Table(String name, String schema, List<Column> columns) {
        this.name = name;
        this.schema = schema;
        this.columns = columns;
    }

    @Override
    public int compareTo(Table o) {
        return this.name.compareTo(o.getName());
    }

    public static Table build(String name) {
        return new Table(name, null, null);
    }

    public static Table build(String name, String schema) {
        return new Table(name, schema, null);
    }

    public static Table build(String name, String schema, List<Column> columns) {
        return new Table(name, schema, columns);
    }

    public String getFlinkTableWith(String flinkConfig) {
        String tableWithSql = "";
        if (Asserts.isNotNullString(flinkConfig)) {
            tableWithSql = SqlUtil.replaceAllParam(flinkConfig, "schemaName", schema);
            tableWithSql = SqlUtil.replaceAllParam(tableWithSql, "tableName", name);
        }
        return tableWithSql;
    }

    public String getFlinkTableSql(String catalogName, String flinkConfig) {
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(catalogName + "." + schema + "." + name + " (\n");
        List<String> pks = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            String type = columns.get(i).getJavaType().getFlinkType();
            sb.append("    ");
            if (i > 0) {
                sb.append(",");
            }
            sb.append("`" + columns.get(i).getName() + "` " + type);
            if (Asserts.isNotNullString(columns.get(i).getComment())) {
                if(columns.get(i).getComment().contains("\'") | columns.get(i).getComment().contains("\"")) {
                    sb.append(" COMMENT '" + columns.get(i).getComment().replaceAll("\"|'","") + "'");
                }else {
                    sb.append(" COMMENT '" + columns.get(i).getComment() + "'");
                }
            }
            sb.append("\n");
            if (columns.get(i).isKeyFlag()) {
                pks.add(columns.get(i).getName());
            }
        }
        StringBuilder pksb = new StringBuilder("PRIMARY KEY ( ");
        for (int i = 0; i < pks.size(); i++) {
            if (i > 0) {
                pksb.append(",");
            }
            pksb.append("`"+pks.get(i)+"`");
        }
        pksb.append(" ) NOT ENFORCED\n");
        if (pks.size() > 0) {
            sb.append("    ,");
            sb.append(pksb);
        }
        sb.append(")");
        if(Asserts.isNotNullString(comment)){
            if(comment.contains("\'") | comment.contains("\"")) {
                sb.append(" COMMENT '" + comment.replaceAll("\"|'","") + "'\n");
            }else {
                sb.append(" COMMENT '" + comment + "'\n");
            }
        }
        sb.append(" WITH (\n");
        sb.append(getFlinkTableWith(flinkConfig));
        sb.append("\n);\n");
        return sb.toString();
    }


    public String getSqlSelect(String catalogName) {
        StringBuilder sb = new StringBuilder("SELECT\n");
        for (int i = 0; i < columns.size(); i++) {
            sb.append("    ");
            if (i > 0) {
                sb.append(",");
            }
            String columnComment= columns.get(i).getComment();
            if(columnComment.contains("\'") | columnComment.contains("\"")) {
                columnComment = columnComment.replaceAll("\"|'","");
            }
            if(Asserts.isNotNullString(columnComment)){
                sb.append("`"+columns.get(i).getName() + "`  --  " + columnComment + " \n");
            }else {
                sb.append("`"+columns.get(i).getName() + "` \n");

            }
        }
        if(Asserts.isNotNullString(comment)){
            sb.append(" FROM " + catalogName + "." + schema + "." + name + ";" + " -- " + comment + "\n");
        }else {
            sb.append(" FROM " + catalogName + "." + schema + "." + name +";\n");
        }
        return sb.toString();
    }
}
