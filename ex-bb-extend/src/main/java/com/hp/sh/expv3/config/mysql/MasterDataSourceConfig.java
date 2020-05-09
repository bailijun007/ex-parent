package com.hp.sh.expv3.config.mysql;

import com.github.pagehelper.PageInterceptor;
import com.hp.sh.expv3.bb.extension.util.SqlSessionFactoryUtil;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;


/**
 * @author BaiLiJun  on 2020/5/8
 */
@Configuration
@MapperScan(basePackages = MasterDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MasterDataSourceConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(MasterDataSourceConfig.class);

    //mapper文件目录
    static final String PACKAGE = "com.hp.sh.expv3.bb.extension.dao";

    //处理器包.
    private static final String TYPE_HANDLERS_PACKAGE = "com.hp.sh.expv3.bb.extension.handler";
    //暂时不用
    private static final String TYPE_ALIASES_PACKAGE = "com.hp.sh.expv3.bb.extension.type";

    //mapper文件目录
    private static final String MAPPER_LOCATIONS = "classpath:mybatis/mapper/extension/*.xml";


    @Primary
    @Bean(name = "masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari.primary")
    public HikariDataSource masterDateSource() {
    return     DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Resource(name = "mybatisConfig")
    private org.apache.ibatis.session.Configuration mybatisConfig;

    @Resource(name = "pageInterceptor")
    private PageInterceptor pageInterceptor;

    @Primary
    @Bean(name = "masterSqlSessionFactory")
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource) throws Exception {
        return SqlSessionFactoryUtil.createSqlSessionFactory(masterDataSource,TYPE_ALIASES_PACKAGE,
                TYPE_HANDLERS_PACKAGE,MAPPER_LOCATIONS,mybatisConfig,new Interceptor[] {pageInterceptor});
    }

    @Primary
    @Bean("masterSqlSessionFactory")
    public SqlSessionTemplate masterSqlSessionTemplate(@Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
