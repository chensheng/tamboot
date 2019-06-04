package com.tamboot.webapp.mybatis;

import com.tamboot.mybatis.annotation.InsertConfig;
import com.tamboot.mybatis.id.SnowFlakeIdGeneratorFactory;
import com.tamboot.mybatis.strategy.SnowFlakeIdInsertStrategy;
import com.tamboot.security.core.TambootUserDetails;
import com.tamboot.security.util.SafeSecurityContextHolder;
import net.sf.jsqlparser.expression.Expression;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateInfoInsertStrategy extends SnowFlakeIdInsertStrategy {

    public CreateInfoInsertStrategy(SnowFlakeIdGeneratorFactory idGeneratorFactory) {
        super(idGeneratorFactory);
    }

    @Override
    public Map<String, Expression> generateExtraInsertColumns(InsertConfig insertConfig) {
        TambootUserDetails userDetails = SafeSecurityContextHolder.getUserDetails();
        if (userDetails == null || userDetails.getUserId() == null) {
            return null;
        }

        Map<String, Expression> extraColumns = new HashMap<String, Expression>();
        extraColumns.put("creator", createLongValue(userDetails.getUserId()));
        extraColumns.put("create_time", createTimestamp(new Date()));
        extraColumns.put("version", createLongValue(0l));
        return extraColumns;
    }

}
