package com.dlink.explainer.ca;

import com.dlink.explainer.trans.Trans;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * ColumnCA
 *
 * @author wenmo
 * @since 2021/6/22
 **/
@Getter
@Setter
public class ColumnCA implements ICA{
    private Integer id;
    private Integer tableId;
    private List<Integer> parentId;
    private String name;
    private String alias;
    private String operation;
    private String columnName;
    private String familyName;
    private String type;
    private String columnType;
    private Trans trans;
    private TableCA tableCA;
    private String tableName;

    public ColumnCA(Integer id, String name, String alias, String columnName, String familyName,String operation, TableCA tableCA,Trans trans) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.columnName = columnName;
        this.familyName = familyName;
        this.operation = operation;
        this.tableCA = tableCA;
        this.tableId = tableCA.getId();
        this.tableName = tableCA.getName();
        this.trans = trans;
        this.type = trans.getPact();
    }

    public ColumnCA(Integer id, List<Integer> parentId, String name, String alias, String columnName, String familyName, String type, TableCA tableCA) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.alias = alias;
        this.columnName = columnName;
        this.familyName = familyName;
        this.type = type;
        this.tableCA = tableCA;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Integer> getParentId() {
        return parentId;
    }

    public void setParentId(List<Integer> parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TableCA getTableCA() {
        return tableCA;
    }

    public void setTableCA(TableCA tableCA) {
        this.tableCA = tableCA;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Trans getTrans() {
        return trans;
    }

    public void setTrans(Trans trans) {
        this.trans = trans;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "ColumnCA{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", columnName='" + columnName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", type='" + type + '\'' +
                ", tableCA=" + tableCA +
                '}';
    }
}
