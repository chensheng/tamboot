package com.tamboot.webapp.mybatis;

import com.tamboot.mybatis.annotation.UpdateConfig;
import com.tamboot.mybatis.strategy.VersionLockUpdateStrategy;
import com.tamboot.security.util.SafeSecurityContextHolder;
import net.sf.jsqlparser.expression.Expression;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ModifyInfoUpdateStrategy extends VersionLockUpdateStrategy {
    @Override
    public Map<String, Expression> generateExtraUpdateColumns(UpdateConfig versionConfig) {
        Map<String, Expression> extraColumns = new HashMap<String, Expression>();
        extraColumns.put("modify_time", this.createTimestamp(new Date()));

        Long userId = SafeSecurityContextHolder.getUserId();
        if (userId != null) {
            extraColumns.put("modifier", this.createLongValue(userId));
        }

        return extraColumns;
    }
}
