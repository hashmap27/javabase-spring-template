package com.javabase.template.springconfig.root;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.javabase.template.config.AppConfig;
import com.javabase.template.framework.mybatis.RefreshableSqlSessionFactoryBean;

/**
 * DataBase 관련 설정
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
@Configuration
@EnableTransactionManagement
public class DataConfig {
    protected static Logger logger = LoggerFactory.getLogger(DataConfig.class);

    @Autowired AppConfig appConfig;

    @Bean(name = "dataSource")
    public DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(appConfig.getJdbcDriverClass());
        dataSource.setUrl(appConfig.getJdbcUrl());
        dataSource.setUsername(appConfig.getJdbcUsername());
        dataSource.setPassword(appConfig.getJdbcPassword());
        return dataSource;
    }

    /** Transaction Manager */
    @Bean(name="transactionManager")
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /** MyBatis SqlsessionTemplate */
    @Bean
    public SqlSessionTemplate sqlSession(SqlSessionFactory sqlFactory) {
        return new SqlSessionTemplate(sqlFactory);
    }

    /** MyBatis SqlSessionFactory (local Profile) */
    @Bean
    @Profile("local")
    public SqlSessionFactoryBean sqlFactoryLocal(DataSource dataSource, final ApplicationContext ctx) throws IOException {
        return setupSqlFactory(new RefreshableSqlSessionFactoryBean(), dataSource, ctx);
    }

    /** MyBatis SqlSessionFactory (Development, Production) */
    @Bean
    @Profile("!local")
    public SqlSessionFactoryBean sqlFactory(DataSource dataSource, final ApplicationContext ctx) throws IOException {
        return setupSqlFactory(new SqlSessionFactoryBean(), dataSource, ctx);
    }
    protected SqlSessionFactoryBean setupSqlFactory(SqlSessionFactoryBean sqlFactory, DataSource dataSource, ApplicationContext ctx) throws IOException {
        sqlFactory.setDataSource(dataSource);
        sqlFactory.setConfigLocation(ctx.getResource("classpath:/myBatisMapper/config/mybatis-config.xml"));
        sqlFactory.setMapperLocations(ctx.getResources("classpath:/myBatisMapper/mapper/**/*.xml"));
        return sqlFactory;
    }
}
