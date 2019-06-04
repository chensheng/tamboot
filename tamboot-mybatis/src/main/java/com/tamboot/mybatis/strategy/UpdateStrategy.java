package com.tamboot.mybatis.strategy;

import com.tamboot.mybatis.annotation.UpdateConfig;
import net.sf.jsqlparser.expression.Expression;

import java.util.Map;

public abstract class UpdateStrategy extends Strategy {
    protected boolean overrideColumn;
    
    protected boolean versionLock = true;

    /**
     * Generate extra update columns those will be added to update sql.
     * @param versoinConfig
     * @return
     */
    public abstract Map<String, Expression> generateExtraUpdateColumns(UpdateConfig versoinConfig);
    
    /**
     * Generate version column value which will be added to update sql.
     * @return
     */
    public abstract Expression generateVersionColumnValue(String versionColumnName, Object oldVersion);

    /**
     * Default database version column name.
     * @return
     */
    public abstract String getDefaultVersionColumnName();

    /**
     * Default is false.
     * @return true to override column even though update sql contains that column.  
     */
    public boolean getOverrideColumn() {
        return overrideColumn;
    }

    /**
     * Default is false.
     * @param overrideColumn true to override column even though update sql contains that column.  
     */
    public void setOverrideColumn(boolean overrideColumn) {
        this.overrideColumn = overrideColumn;
    }
    
    /**
     * Default is true.
     * @return
     */
    public boolean getVersionLock() {
        return versionLock;
    }

    /**
     * Default is true.
     * @param versionLock
     */
    public void setVersionLock(boolean versionLock) {
        this.versionLock = versionLock;
    }
}
