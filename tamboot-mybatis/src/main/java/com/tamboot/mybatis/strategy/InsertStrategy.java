package com.tamboot.mybatis.strategy;

import com.tamboot.mybatis.annotation.InsertConfig;
import net.sf.jsqlparser.expression.Expression;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.Map;

public abstract class InsertStrategy extends Strategy {
    
    protected boolean overrideColumn;
    
    /**
     * Generate extra columns those will be added to insert sql.
     * @param insertConfig may be null if the annotation not declared at mapper method.
     * @return extra insert columns map. Key is column name, value is column value.
     */
    public abstract Map<String, Expression> generateExtraInsertColumns(InsertConfig insertConfig);
    
    /**
     * Generate an ID value which will be added to insert sql.
     * @param tableName
     * @param ms
     * @param executor
     * @return
     */
    public abstract Expression generateIdColumnValue(String tableName, MappedStatement ms, Executor executor);
    
    /**
     * Get default ID column name in database.
     * @return
     */
    public abstract String getDefaultIdColumnName();
    
    /**
     * Default is false.
     * @return true to override column even though insert sql contains the column.  
     */
    public boolean getOverrideColumn() {
        return overrideColumn;
    }
    
    /**
     * Default is false.
     * @param overrideColumn true to override column even though insert sql contains the column.  
     */
    public void setOverrideColumn(boolean overrideColumn) {
        this.overrideColumn = overrideColumn;
    }

}
