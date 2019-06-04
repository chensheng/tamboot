package com.tamboot.mybatis.strategy;

import net.sf.jsqlparser.expression.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Strategy {
    private ThreadLocal<SimpleDateFormat> threadLocalDatetimeFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss ");
        }
    };
    
    protected NullValue createNullValue() {
        return new NullValue();
    }
    
    protected TimestampValue createTimestamp(Date date) {
        String dateVal = threadLocalDatetimeFormat.get().format(date);
        return new TimestampValue(dateVal);
    }
    
    protected StringValue createStringValue(String str) {
        return new StringValue(" " + str + " ");
    }
    
    protected DoubleValue createDoubleValue(Double val) {
        if (val == null) {
            val = 0d;
        }
        
        return new DoubleValue(String.valueOf(val));
    }
    
    protected LongValue createLongValue(Long val) {
        if (val == null) {
            val = 0l;
        }
        return new LongValue(String.valueOf(val));
    }
    
    protected HexValue createHexValue(String val) {
        if (val == null) {
            val = "";
        }
        
        return new HexValue(val);
    }
}
