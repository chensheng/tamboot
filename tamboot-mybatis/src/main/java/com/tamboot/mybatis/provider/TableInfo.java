package com.tamboot.mybatis.provider;

import java.io.Serializable;
import java.lang.reflect.Field;

public class TableInfo implements Serializable {
    private String tableName;

    private String idColumn;

    private String idFieldName;

    private String[] ColumnsInSql;

    private Field[] fieldsInSql;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIdColumn() {
        return idColumn;
    }

    public void setIdColumn(String idColumn) {
        this.idColumn = idColumn;
    }

    public String getIdFieldName() {
        return idFieldName;
    }

    public void setIdFieldName(String idFieldName) {
        this.idFieldName = idFieldName;
    }

    public String[] getColumnsInSql() {
        return ColumnsInSql;
    }

    public void setColumnsInSql(String[] columnsInSql) {
        ColumnsInSql = columnsInSql;
    }

    public Field[] getFieldsInSql() {
        return fieldsInSql;
    }

    public void setFieldsInSql(Field[] fieldsInSql) {
        this.fieldsInSql = fieldsInSql;
    }
}
