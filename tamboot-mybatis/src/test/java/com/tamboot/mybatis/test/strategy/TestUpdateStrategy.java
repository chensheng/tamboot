package com.tamboot.mybatis.test.strategy;

import com.tamboot.mybatis.annotation.UpdateConfig;
import com.tamboot.mybatis.strategy.VersionLockUpdateStrategy;
import net.sf.jsqlparser.expression.Expression;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestUpdateStrategy extends VersionLockUpdateStrategy {
	@Override
	public Map<String, Expression> generateExtraUpdateColumns(UpdateConfig versoinConfig) {
		Map<String, Expression> extraColumns = new HashMap<String, Expression>();
		extraColumns.put("modify_time", this.createTimestamp(new Date()));
		extraColumns.put("modifier", this.createLongValue(444L));
		return extraColumns;
	}
}
