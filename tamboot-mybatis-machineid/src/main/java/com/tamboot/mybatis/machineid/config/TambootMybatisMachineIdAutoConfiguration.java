package com.tamboot.mybatis.machineid.config;

import com.tamboot.mybatis.config.TambootMybatisAutoConfiguration;
import com.tamboot.mybatis.machineid.core.DynamicMachineIdStrategy;
import com.tamboot.mybatis.machineid.core.MachineIdRedisTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableConfigurationProperties(TambootMybatisDynamicMachineIdProperties.class)
@AutoConfigureBefore(TambootMybatisAutoConfiguration.class)
public class TambootMybatisMachineIdAutoConfiguration {
    private TambootMybatisDynamicMachineIdProperties properties;

    public TambootMybatisMachineIdAutoConfiguration(TambootMybatisDynamicMachineIdProperties properties) {
        this.properties = properties;
    }

    @Bean
    public MachineIdRedisTemplate machineIdRedisTemplate(RedisTemplate redisTemplate) {
        return new MachineIdRedisTemplate(redisTemplate);
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public DynamicMachineIdStrategy dynamicMachineIdStrategy(MachineIdRedisTemplate machineIdRedisTemplate) {
        return new DynamicMachineIdStrategy(machineIdRedisTemplate, properties);
    }
}
