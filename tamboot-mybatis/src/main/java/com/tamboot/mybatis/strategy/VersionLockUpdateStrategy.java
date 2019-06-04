package com.tamboot.mybatis.strategy;

import com.tamboot.mybatis.annotation.UpdateConfig;
import net.sf.jsqlparser.expression.Expression;

import java.util.Map;

public class VersionLockUpdateStrategy extends UpdateStrategy {
	private String versionColumnName = "version";

	public VersionLockUpdateStrategy() {
	}

	public VersionLockUpdateStrategy(String versionColumnName) {
		this.versionColumnName = versionColumnName;
	}

	@Override
	public Map<String, Expression> generateExtraUpdateColumns(UpdateConfig versionConfig) {
		return null;
	}

	@Override
	public Expression generateVersionColumnValue(String versionColumnName, Object oldVersion) {
		if (oldVersion == null) {
            String expression = String.format("%s + 1", versionColumnName);
            return createHexValue(expression);    
        }
        
        Long newVersion = (Long) oldVersion + 1;
        return createLongValue(newVersion);
	}

	@Override
	public String getDefaultVersionColumnName() {
		return versionColumnName;
	}

}
