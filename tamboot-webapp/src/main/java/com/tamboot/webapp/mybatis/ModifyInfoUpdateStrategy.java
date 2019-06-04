package com.tamboot.webapp.mybatis;

import com.tamboot.mybatis.annotation.UpdateConfig;
import com.tamboot.mybatis.strategy.VersionLockUpdateStrategy;
import com.tamboot.security.core.TambootUserDetails;
import com.tamboot.security.util.SafeSecurityContextHolder;
import net.sf.jsqlparser.expression.Expression;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ModifyInfoUpdateStrategy extends VersionLockUpdateStrategy {
    @Override
    public Map<String, Expression> generateExtraUpdateColumns(UpdateConfig versionConfig) {
        TambootUserDetails userDetails = SafeSecurityContextHolder.getUserDetails();
        if (userDetails == null || userDetails.getUserId() == null) {
            return null;
        }

        Map<String, Expression> extraColumns = new HashMap<String, Expression>();
        extraColumns.put("modifier", this.createLongValue(userDetails.getUserId()));
        extraColumns.put("modify_time", this.createTimestamp(new Date()));
        return extraColumns;
    }
}
