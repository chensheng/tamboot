package com.tamboot.mybatis.id;

import com.tamboot.mybatis.config.TambootMybatisProperties;
import org.springframework.util.Assert;

public class StaticMachineIdStrategy extends MachineIdStrategy {
    private TambootMybatisProperties properties;

    public StaticMachineIdStrategy(TambootMybatisProperties properties) {
        Assert.notNull(properties, "properties must not be null");
        this.properties = properties;
    }

    @Override
    protected long generateMachineId() {
        Assert.notNull(properties.getSnowFlake(), "mybatis.snowFlake must not be null");
        Assert.notNull(properties.getSnowFlake().getMachineId(), "mybatis.snowFlake.machineId must not be null");
        return properties.getSnowFlake().getMachineId();
    }
}
