package com.tamboot.mybatis.interceptor;

import com.tamboot.common.utils.ExceptionUtils;
import com.tamboot.mybatis.annotation.InsertConfig;
import com.tamboot.mybatis.config.TambootMybatisProperties;
import com.tamboot.mybatis.id.SnowFlakeIdGeneratorFactory;
import com.tamboot.mybatis.strategy.InsertStrategy;
import com.tamboot.mybatis.strategy.SnowFlakeIdInsertStrategy;
import com.tamboot.mybatis.utils.MetaObjectUtils;
import com.tamboot.mybatis.utils.PluginUtils;
import com.tamboot.mybatis.utils.SqlExpressionUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import org.apache.ibatis.executor.Executor;
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
public class InsertInterceptor implements Interceptor {
	private static final Logger logger = LoggerFactory.getLogger(InsertInterceptor.class);

	private InsertStrategy insertStrategy;

	private TambootMybatisProperties properties;

	public InsertInterceptor(ObjectProvider<InsertStrategy> insertStrategyProvider, SnowFlakeIdGeneratorFactory idGeneratorFactory, TambootMybatisProperties properties) {
	    this.insertStrategy = insertStrategyProvider.getIfAvailable();
	    if (this.insertStrategy == null) {
	        this.insertStrategy = new SnowFlakeIdInsertStrategy(idGeneratorFactory);
        }
	    this.properties = properties;
	    logger.debug("Insert strategy is: {}", this.insertStrategy.getClass());
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
    	if (ignoreInterceptor()) {
    		return invocation.proceed();
    	}
    	
        if (insertStrategy == null) {
        	return invocation.proceed();
        }
        
        StatementHandler statementHandler = (StatementHandler) PluginUtils.processTarget(invocation.getTarget());
        MetaObject statementHandlerMetaObject = MetaObjectUtils.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) statementHandlerMetaObject.getValue("delegate.mappedStatement");
        
        if (SqlCommandType.INSERT != mappedStatement.getSqlCommandType()) {
            return invocation.proceed();
        }
        
        BoundSql boundSql = (BoundSql) statementHandlerMetaObject.getValue("delegate.boundSql");
        Object paramObj = boundSql.getParameterObject();
        
        InsertConfig insertConfig = PluginUtils.findMapperMethodAnnotation(mappedStatement, InsertConfig.class, paramObj);
        if (insertConfig != null && insertConfig.ignoreInterceptor()) {
            return invocation.proceed();
        }
        
        String rawSql = boundSql.getSql();
        Statement statement = CCJSqlParserUtil.parse(rawSql);
        Insert insert = (Insert) statement;
        String tableName = insert.getTable().getName();
        List<Column> columnList = insert.getColumns();
        List<Expression> expressionList = ((ExpressionList) insert.getItemsList()).getExpressions();
        
        Executor executor = (Executor) statementHandlerMetaObject.getValue("delegate.executor");
        Map<String, Integer> columnIndexMap = this.createColumnIndexMap(columnList);
        Map<String, Expression> idColumnMap = this.insertIdColumnInNeed(tableName, insertConfig, mappedStatement, executor, columnList, expressionList, columnIndexMap);
        Map<String, Expression> extraColumnMap = this.insertExtraColumns(insertConfig, columnList, expressionList, columnIndexMap);
        
        String injectedSql = insert.toString();
        statementHandlerMetaObject.setValue("delegate.boundSql.sql", injectedSql);
        
        Object insertResult = invocation.proceed();  
        this.populateParamObj(insertConfig, paramObj, idColumnMap, extraColumnMap);
        
        return insertResult;
    }
    
    private Map<String, Expression> insertIdColumnInNeed(String tableName, InsertConfig insertConfig, MappedStatement ms, Executor executor, List<Column> columnList, List<Expression> expressionList, Map<String, Integer> columnIndexMap) {
        if (insertConfig != null && !insertConfig.autoInsertId()) {
            return null;
        }
        
        String idColumnName = this.resolveIdColumnName(insertConfig);
        if (idColumnName == null) {
            return null;
        }
        
        Expression idColumnValue = insertStrategy.generateIdColumnValue(tableName, ms, executor);
        if (idColumnValue == null) {
            return null;
        }
        
        Map<String, Expression> resultMap = new HashMap<String, Expression>();
        
        if (!columnIndexMap.containsKey(idColumnName)) {
            columnList.add(new Column(idColumnName));
            expressionList.add(idColumnValue);
            resultMap.put(idColumnName, idColumnValue);
            return resultMap;
        }
        
        boolean needOverrideColumn = this.needOverrideColumn(insertConfig);
        if (columnIndexMap.containsKey(idColumnName) && needOverrideColumn) {
            int columnIndex = columnIndexMap.get(idColumnName);
            expressionList.set(columnIndex, idColumnValue);
            resultMap.put(idColumnName, idColumnValue);
            return resultMap;
        }
        
        return resultMap;
    }
    
    private Map<String, Expression> insertExtraColumns(InsertConfig insertConfig,  List<Column> columnList, List<Expression> expressionList, Map<String, Integer> columnIndexMap) {
        Map<String, Expression> extraInsertColumns = insertStrategy.generateExtraInsertColumns(insertConfig);
        if (extraInsertColumns == null || extraInsertColumns.size() == 0) {
            return null;
        }
        
        Map<String, Expression> resultMap = new HashMap<String, Expression>();
        
        boolean needOverrideColumn = this.needOverrideColumn(insertConfig);
        Iterator<Entry<String, Expression>> it = extraInsertColumns.entrySet().iterator();
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
    
    private void populateParamObj(InsertConfig insertConfig, Object paramObj, Map<String, Expression> idMap, Map<String, Expression> extraMap) {
        if (idMap == null && extraMap == null) {
            return;
        }
        
        Map<String, Expression> paramMap = new HashMap<String, Expression>();
        if (idMap != null) {
            paramMap.putAll(idMap);
        }
        if (extraMap != null) {
            paramMap.putAll(extraMap);
        }
        
        MetaObject paramMetaObject = MetaObjectUtils.forObject(paramObj);
        Map<String, String> columnAndFieldMap = this.resolveColumnAndFiledMap(insertConfig);
        
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
            	logger.error(ExceptionUtils.getStackTraceAsString(e));
            }
        }
    }
    
    private Map<String, String> resolveColumnAndFiledMap(InsertConfig insertConfig) {
        Map<String, String> columnAndFieldMap = new HashMap<String, String>();
        if (insertConfig == null) {
            return columnAndFieldMap;
        }
        
        String[] columnAndFieldArr = insertConfig.columnAndFieldMapping();
        if (columnAndFieldArr != null && columnAndFieldArr.length > 0 && columnAndFieldArr.length % 2 == 0) {
            for (int i = 1; i < columnAndFieldArr.length; i+=2) {
                String columnName = columnAndFieldArr[i - 1].toUpperCase();
                String fieldName = columnAndFieldArr[i];
                columnAndFieldMap.put(columnName, fieldName);
            }
        }
        
        return columnAndFieldMap;
    }
    
    private boolean needOverrideColumn(InsertConfig insertConfig) {
        if (insertConfig != null) {
            return insertConfig.overrideColumn();
        }
        
        return insertStrategy.getOverrideColumn();
    }
    
    private String resolveIdColumnName(InsertConfig insertConfig) {
        String idColumnName = insertStrategy.getDefaultIdColumnName();
        
        if (insertConfig != null && !"".equals(insertConfig.idColumnName())) {
            idColumnName = insertConfig.idColumnName();
        }
        
        if (idColumnName != null) {
            idColumnName = idColumnName.toUpperCase();
        }
        
        return idColumnName;
    }
    
    private Map<String, Integer> createColumnIndexMap(List<Column> columnList) {
        Map<String, Integer> columnIndexMap = new HashMap<String, Integer>(columnList.size());
        
        for (int i = 0; i < columnList.size(); i++) {
            Column column = columnList.get(i);
            columnIndexMap.put(column.getColumnName().toUpperCase(), i);
        }
        
        return columnIndexMap;
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
