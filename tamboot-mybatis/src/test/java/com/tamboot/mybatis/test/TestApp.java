package com.tamboot.mybatis.test;

import com.tamboot.mybatis.config.TambootMybatisAutoConfiguration;
import com.tamboot.mybatis.id.SnowFlakeIdGeneratorFactory;
import com.tamboot.mybatis.test.service.TransactionService;
import com.tamboot.mybatis.test.service.impl.TransactionServiceImpl;
import com.tamboot.mybatis.test.strategy.TestInsertStrategy;
import com.tamboot.mybatis.test.strategy.TestUpdateStrategy;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Import({DataSourceConfig.class, MybatisAutoConfiguration.class, TambootMybatisAutoConfiguration.class})
@MapperScan("com.tamboot.mybatis.test.mapper")
@EnableTransactionManagement
public class TestApp {
    @Bean
    public TestInsertStrategy testInsertStrategy(SnowFlakeIdGeneratorFactory idGeneratorFactory) {
        return new TestInsertStrategy(idGeneratorFactory);
    }

    @Bean
    public TestUpdateStrategy testUpdateStrategy() {
        return new TestUpdateStrategy();
    }

    @Bean
    public TransactionService transactionService() {
        return new TransactionServiceImpl();
    }
}
