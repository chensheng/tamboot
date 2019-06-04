package com.tamboot.mybatis.interceptor;

import com.tamboot.mybatis.annotation.UpdateConfig;
import com.tamboot.mybatis.config.TambootMybatisProperties;
import com.tamboot.mybatis.exception.VersionLockException;
import com.tamboot.mybatis.utils.MetaObjectUtils;
import com.tamboot.mybatis.utils.PluginUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Properties;

@Intercepts({
    @Signature(type= StatementHandler.class, method="update", args={java.sql.Statement.class})
})
public class UpdateResultInterceptor implements Interceptor {
	private TambootMybatisProperties properties;

	public UpdateResultInterceptor(TambootMybatisProperties properties) {
	    this.properties = properties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
    	if (this.ignoreInterceptor()) {
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
        Object result = invocation.proceed();
        this.checkUpdateResult(result, rawSql);
        return result;
    }
    
    private void checkUpdateResult(Object updateResult, String rawSql) {
    	if (properties == null || !properties.getThrowVersionLockException()) {
    		return;
    	}
    	
    	if (updateResult == null || (!(updateResult instanceof Integer) && !(updateResult instanceof Long))) {
    		return;
    	}
    	
    	long updatedCount = 0;
    	if (updateResult instanceof Integer) {
    		updatedCount = (Integer) updateResult;
    	} else if (updateResult instanceof Long) {
    		updatedCount = (Long) updateResult;
    	}
    	
    	if (updatedCount <= 0) {
    		throw new VersionLockException("Version lock fail: " + rawSql);
    	}
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
