package com.tamboot.mybatis.config;

import com.github.pagehelper.PageInterceptor;
import com.tamboot.mybatis.id.SnowFlakeIdGeneratorFactory;
import com.tamboot.mybatis.interceptor.InsertInterceptor;
import com.tamboot.mybatis.interceptor.UpdateInterceptor;
import com.tamboot.mybatis.interceptor.UpdateResultInterceptor;
import com.tamboot.mybatis.strategy.InsertStrategy;
import com.tamboot.mybatis.strategy.UpdateStrategy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ConditionalOnClass(SqlSessionFactory.class)
@EnableConfigurationProperties(TambootMybatisProperties.class)
@AutoConfigureBefore(MybatisAutoConfiguration.class)
public class TambootMybatisAutoConfiguration implements InitializingBean {
    private final TambootMybatisProperties properties;

    public TambootMybatisAutoConfiguration(TambootMybatisProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.checkSnowFlakeProperties();
    }

    private void checkSnowFlakeProperties() {
        if (properties.getSnowFlake() == null || properties.getSnowFlake().getDataCenterId() == null) {
            throw new NullPointerException("mybatis.dataCenterId must not be null, please check your configuration.");
        }
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    @Bean
    public SnowFlakeIdGeneratorFactory snowFlakeIdGeneratorFactory() {
        return new SnowFlakeIdGeneratorFactory(properties.getSnowFlake().getDataCenterId(), properties.getSnowFlake().getGeneratorStartTime());
    }

    @Bean
    public InsertInterceptor insertInterceptor(SnowFlakeIdGeneratorFactory idGeneratorFactory, ObjectProvider<InsertStrategy> insertStrategyProvider) {
        return new InsertInterceptor(insertStrategyProvider, idGeneratorFactory, properties);
    }

    @Bean
    public UpdateInterceptor updateInterceptor(ObjectProvider<UpdateStrategy> updateStrategyProvider) {
        return new UpdateInterceptor(updateStrategyProvider, properties);
    }

    @Bean
    public UpdateResultInterceptor updateResultInterceptor() {
        return new UpdateResultInterceptor(properties);
    }

    @Bean
    @ConditionalOnMissingBean(PageInterceptor.class)
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties props = new Properties();
        props.setProperty("reasonable", "true");
        props.setProperty("supportMethodsArguments", "true");
        pageInterceptor.setProperties(props);
        return pageInterceptor;
    }

}
