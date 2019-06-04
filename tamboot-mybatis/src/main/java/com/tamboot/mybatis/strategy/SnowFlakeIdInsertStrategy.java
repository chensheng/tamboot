package com.tamboot.mybatis.strategy;

import com.tamboot.mybatis.annotation.InsertConfig;
import com.tamboot.mybatis.id.SnowFlakeIdGeneratorFactory;
import net.sf.jsqlparser.expression.Expression;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.springframework.util.StringUtils;

import java.util.Map;

public class SnowFlakeIdInsertStrategy extends InsertStrategy {
    private SnowFlakeIdGeneratorFactory idGeneratorFactory;

    private String idColumnName = "id";

    public SnowFlakeIdInsertStrategy(SnowFlakeIdGeneratorFactory idGeneratorFactory) {
        this.idGeneratorFactory = idGeneratorFactory;
    }

    public SnowFlakeIdInsertStrategy(SnowFlakeIdGeneratorFactory idGeneratorFactory, String idColumnName) {
        this.idGeneratorFactory = idGeneratorFactory;
        if (!StringUtils.isEmpty(idColumnName)) {
            this.idColumnName = idColumnName;
        }
    }

    @Override
    public Map<String, Expression> generateExtraInsertColumns(InsertConfig insertConfig) {
        return null;
    }

    @Override
    public Expression generateIdColumnValue(String tableName, MappedStatement ms, Executor executor) {
        long id = idGeneratorFactory.nextId(tableName);
        return createLongValue(id);
    }

    @Override
    public String getDefaultIdColumnName() {
        return idColumnName;
    }
}
