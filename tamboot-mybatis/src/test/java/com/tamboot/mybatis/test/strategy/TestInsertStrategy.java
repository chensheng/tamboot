package com.tamboot.mybatis.test.strategy;

import com.tamboot.mybatis.annotation.InsertConfig;
import com.tamboot.mybatis.id.SnowFlakeIdGeneratorFactory;
import com.tamboot.mybatis.strategy.SnowFlakeIdInsertStrategy;
import net.sf.jsqlparser.expression.Expression;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestInsertStrategy extends SnowFlakeIdInsertStrategy {
	public TestInsertStrategy(SnowFlakeIdGeneratorFactory idGeneratorFactory) {
		super(idGeneratorFactory);
	}

	@Override
	public Map<String, Expression> generateExtraInsertColumns(InsertConfig insertConfig) {
		Map<String, Expression> extraColumns = new HashMap<String, Expression>();
		extraColumns.put("create_time", this.createTimestamp(new Date()));
		extraColumns.put("creator", this.createLongValue(333l));
		extraColumns.put("modify_time", this.createTimestamp(new Date()));
		extraColumns.put("modifier", this.createLongValue(333l));
		extraColumns.put("version", this.createLongValue(0l));
		return extraColumns;
	}
}
