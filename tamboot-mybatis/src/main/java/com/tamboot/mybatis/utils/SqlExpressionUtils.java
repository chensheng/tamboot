package com.tamboot.mybatis.utils;

import net.sf.jsqlparser.expression.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.Timestamp;

public class SqlExpressionUtils {
	private static final Logger logger = LoggerFactory.getLogger(SqlExpressionUtils.class);

    public static Object decode(Expression expression) {
        if (expression == null) {
            return null;
        }
        
        Object result = null;
        
        if (expression instanceof StringValue) {
            result = ((StringValue) expression).getValue();
        } if (expression instanceof DateValue) {
            Date date = ((DateValue) expression).getValue();
            result = new java.util.Date(date.getTime());
        } else if (expression instanceof TimestampValue) {
            Timestamp timestamp = ((TimestampValue) expression).getValue();
            result = new java.util.Date(timestamp.getTime());
        } else if (expression instanceof DoubleValue) {
            result = ((DoubleValue) expression).getValue();
        } else if (expression instanceof LongValue) {
            result = ((LongValue) expression).getValue();
        } else {
        	logger.error("Could not decode expression {}", expression.getClass());
        }
         
        return result;
    }
}
