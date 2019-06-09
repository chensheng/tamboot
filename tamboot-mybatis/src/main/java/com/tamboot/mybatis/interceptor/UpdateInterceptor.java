package com.tamboot.mybatis.interceptor;

import com.tamboot.common.utils.ExceptionUtils;
import com.tamboot.mybatis.annotation.UpdateConfig;
import com.tamboot.mybatis.config.TambootMybatisProperties;
import com.tamboot.mybatis.strategy.UpdateStrategy;
import com.tamboot.mybatis.strategy.VersionLockUpdateStrategy;
import com.tamboot.mybatis.utils.MetaObjectUtils;
import com.tamboot.mybatis.utils.PluginUtils;
import com.tamboot.mybatis.utils.SqlExpressionUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import java.sql.Connection;
import java.util.*;
import java.util.Map.Entry;

@Intercepts({
    @Signature(type= StatementHandler.class, method="prepare", args={Connection.class, Integer.class})
})
public class UpdateInterceptor implements Interceptor {
	private static final Logger logger = LoggerFactory.getLogger(UpdateInterceptor.class);
	
	private UpdateStrategy updateStrategy;

	private TambootMybatisProperties properties;

	public UpdateInterceptor(ObjectProvider<UpdateStrategy> updateStrategyProvider, TambootMybatisProperties properties) {
	    this.updateStrategy = updateStrategyProvider.getIfAvailable();
	    if (this.updateStrategy == null) {
	        this.updateStrategy = new VersionLockUpdateStrategy();
        }
	    this.properties = properties;
	    logger.debug("Update strategy is: {}", this.updateStrategy.getClass());
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
    	if (this.ignoreInterceptor()) {
    		return invocation.proceed();
    	}
    	
        if (updateStrategy == null) {
        	return invocation.proceed();
        }
        
        StatementHandler statementHandler = (StatementHandler) PluginUtils.processTarget(invocation.getTarget());
        MetaObject statementHandlerMetaObject = MetaObjectUtils.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) statementHandlerMetaObject.getValue("delegate.mappedStatement");
        
        if (SqlCommandType.UPDATE != mappedStatement.getSqlCommandType()) {
            return invocation.proceed();
        }
        
        BoundSql boundSql = (BoundSql) statementHandlerMetaObject.getValue("delegate.boundSql");
        Object paramObj = boundSql.getParameterObject();
        
        UpdateConfig updateConfig = PluginUtils.findMapperMethodAnnotation(mappedStatement, UpdateConfig.class, paramObj);
        if (updateConfig != null && updateConfig.ignoreInterceptor()) {
            return invocation.proceed();
        }
        
        String rawSql = boundSql.getSql();
        Statement statement = CCJSqlParserUtil.parse(rawSql);
        Update update = (Update) statement;
        
        List<Column> columnList = update.getColumns();
        List<Expression> expressionList = update.getExpressions();
        Map<String, Integer> columnIndexMap = this.createColumnIndexMap(columnList);
        Map<String, String> columnAndFieldMap = this.resolveColumnAndFiledMap(updateConfig);
        
        this.addVersionLock(updateConfig, update, paramObj, columnAndFieldMap);
        Map<String, Expression> versionMap = this.updateVersionColumn(updateConfig, paramObj, columnAndFieldMap, columnList, expressionList, columnIndexMap);
        Map<String, Expression> extraMap = this.updateExtraColumns(updateConfig, columnList, expressionList, columnIndexMap);
        
        String injectedSql = update.toString();
        statementHandlerMetaObject.setValue("delegate.boundSql.sql", injectedSql);
        
        Object result = invocation.proceed();
        this.populateParamObj(updateConfig, paramObj, columnAndFieldMap, versionMap, extraMap);
        
        return result;
    }
    
    private Map<String, Expression> updateVersionColumn(UpdateConfig updateConfig, Object paramObj, Map<String, String> columnAndFieldMap, List<Column> columnList, List<Expression> expressionList, Map<String, Integer> columnIndexMap) {
        boolean needVersionLock = this.needVersionLock(updateConfig);
    	if (!needVersionLock) {
    		return null;
    	}
        
    	String versionColumnName = this.resolveVersionColumnName(updateConfig);
        if (versionColumnName == null || "".equals(versionColumnName)) {
            return null;
        }
        
        MetaObject paramMetaObject = MetaObjectUtils.forObject(paramObj);
        String versionFieldName = this.resolveFieldName(versionColumnName, columnAndFieldMap);
        Object versionFieldVal = null;
        try {
        	versionFieldVal = paramMetaObject.getValue(versionFieldName);
        } catch (Exception e) {
        	logger.debug(ExceptionUtils.getStackTraceAsString(e));
        	return null;
        }
        
        Expression versionColumnValue = updateStrategy.generateVersionColumnValue(versionColumnName, versionFieldVal);
        if (versionColumnValue == null) {
            return null;
        }
        
        Map<String, Expression> result = new HashMap<String, Expression>();
        if (!columnIndexMap.containsKey(versionColumnName)) {
            columnList.add(new Column(versionColumnName));
            expressionList.add(versionColumnValue);
            result.put(versionColumnName, versionColumnValue);
            return result;
        }
        
        boolean needOverrideColumn = this.needOverrideColumn(updateConfig);
        if (columnIndexMap.containsKey(versionColumnName) && needOverrideColumn) {
            int versionColumnIndex = columnIndexMap.get(versionColumnName);
            expressionList.set(versionColumnIndex, versionColumnValue);
            result.put(versionColumnName, versionColumnValue);
            return result;
        }
        
        return result;
    }
    
    private void addVersionLock(UpdateConfig updateConfig, Update update, Object paramObj, Map<String, String> columnAndFieldMap) {
        boolean needVersionLock = this.needVersionLock(updateConfig);
        if (!needVersionLock) {
            return;
        }
        
        String versionColumnName = this.resolveVersionColumnName(updateConfig);
        if (versionColumnName == null || "".equals(versionColumnName)) {
            return;
        }
        
        MetaObject paramMetaObject = MetaObjectUtils.forObject(paramObj);
        String fieldName = this.resolveFieldName(versionColumnName, columnAndFieldMap);
        Object fieldVal = null;
        try {
        	fieldVal = paramMetaObject.getValue(fieldName);
        } catch (Exception e) {
        	logger.debug(ExceptionUtils.getStackTraceAsString(e));
        }
        if (fieldVal == null) {
            return;
        }
        
        String versionLockCdt = String.format(" AND %s = '%s'", versionColumnName, fieldVal.toString());
        Expression whereExpression = update.getWhere();
        
        String newWhere = whereExpression.toString() + versionLockCdt;
        update.setWhere(new HexValue(newWhere));
    }
    
    private Map<String, Expression> updateExtraColumns(UpdateConfig updateConfig,  List<Column> columnList, List<Expression> expressionList, Map<String, Integer> columnIndexMap) {
        Map<String, Expression> extraUpdateColumns = updateStrategy.generateExtraUpdateColumns(updateConfig);
        if (extraUpdateColumns == null || extraUpdateColumns.size() == 0) {
            return null;
        }
        
        Map<String, Expression> resultMap = new HashMap<String, Expression>();
        
        boolean needOverrideColumn = this.needOverrideColumn(updateConfig);
        Iterator<Entry<String, Expression>> it = extraUpdateColumns.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Expression> entry = it.next();
            String columnName = entry.getKey().toUpperCase();
            Expression columnValue = entry.getValue();
            
            if (!columnIndexMap.containsKey(columnName)) {
                columnList.add(new Column(columnName));
                expressionList.add(columnValue);
                resultMap.put(columnName, columnValue);
                continue;
            }
            
            if (columnIndexMap.containsKey(columnName) && needOverrideColumn) {
                int columnIndex = columnIndexMap.get(columnName);
                expressionList.set(columnIndex, columnValue);
                resultMap.put(columnName, columnValue);
                continue;
            }
        }
        
        return resultMap;
    }
    
    private void populateParamObj(UpdateConfig updateConfig, Object paramObj, Map<String, String> columnAndFieldMap, Map<String, Expression> versionMap, Map<String, Expression> extraMap) {
        if (versionMap == null && extraMap == null) {
            return;
        }
        
        Map<String, Expression> paramMap = new HashMap<String, Expression>();
        if (versionMap != null) {
            paramMap.putAll(versionMap);
        }
        if (extraMap != null) {
            paramMap.putAll(extraMap);
        }
        
        MetaObject paramMetaObject = MetaObjectUtils.forObject(paramObj);
        
        Iterator<Entry<String, Expression>> it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Expression> entry = it.next();
            String columnName = entry.getKey();
            Expression columnVal = entry.getValue();
            
            Object fieldVal = SqlExpressionUtils.decode(columnVal);
            if (fieldVal == null) {
                continue;
            }
            
            String fieldName = null;
            if (columnAndFieldMap.containsKey(columnName.toUpperCase())) {
                fieldName = columnAndFieldMap.get(columnName.toUpperCase());
            } else {
                fieldName = PluginUtils.underscoreToCamel(columnName);
            }
            
            try {
                paramMetaObject.setValue(fieldName, fieldVal);    
            } catch (Exception e) {
            	logger.debug(ExceptionUtils.getStackTraceAsString(e));
            }
        }
    }
    
    private String resolveFieldName(String columnName, Map<String, String> columnAndFieldMap) {
        String fieldName = null;
        if (columnAndFieldMap.containsKey(columnName.toUpperCase())) {
            fieldName = columnAndFieldMap.get(columnName.toUpperCase());
        } else {
            fieldName = PluginUtils.underscoreToCamel(columnName);
        }
        return fieldName;
    }
    
    
    private Map<String, Integer> createColumnIndexMap(List<Column> columnList) {
        Map<String, Integer> columnIndexMap = new HashMap<String, Integer>();
        
        for (int i = 0; i < columnList.size(); i++) {
            Column column = columnList.get(i);
            columnIndexMap.put(column.getColumnName().toUpperCase(), i);
        }
        
        return columnIndexMap;
    }
    
    private String resolveVersionColumnName(UpdateConfig updateConfig) {
        String versionColumnName = updateStrategy.getDefaultVersionColumnName();
        if (updateConfig != null && updateConfig.versionColumnName() != null 
                && !"".equals(updateConfig.versionColumnName())) {
            versionColumnName = updateConfig.versionColumnName();
        }
        
        if (versionColumnName != null) {
            versionColumnName = versionColumnName.toUpperCase();
        }
        
        return versionColumnName;
    }
    
    private Map<String, String> resolveColumnAndFiledMap(UpdateConfig updateConfig) {
        Map<String, String> columnAndFieldMap = new HashMap<String, String>();
        if (updateConfig == null) {
            return columnAndFieldMap;
        }
        
        String[] columnAndFieldArr = updateConfig.columnAndFieldMapping();
        if (columnAndFieldArr != null && columnAndFieldArr.length > 0 && columnAndFieldArr.length % 2 == 0) {
            for (int i = 1; i < columnAndFieldArr.length; i+=2) {
                String columnName = columnAndFieldArr[i - 1].toUpperCase();
                String fieldName = columnAndFieldArr[i];
                columnAndFieldMap.put(columnName, fieldName);
            }
        }
        
        return columnAndFieldMap;
    }
    
    private boolean needOverrideColumn(UpdateConfig updateConfig) {
        if (updateConfig != null) {
            return updateConfig.overrideColumn();
        }
        
        return updateStrategy.getOverrideColumn();
    }
    
    private boolean needVersionLock(UpdateConfig updateConfig) {
        if (updateConfig != null) {
            return updateConfig.versionLock();
        }
        
        return updateStrategy.getVersionLock();
    }

    @Override
    public Object plugin(Object paramObject) {
        return Plugin.wrap(paramObject, this);
    }

    @Override
    public void setProperties(Properties props) {
       
    }

    private boolean ignoreInterceptor() {
    	if (properties != null && properties.getIgnoreInterceptor()) {
			return true;
		}
    	
    	return false;
    }
}
