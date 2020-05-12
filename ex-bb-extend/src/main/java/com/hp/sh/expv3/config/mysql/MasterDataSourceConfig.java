package com.hp.sh.expv3.config.mysql;

import com.github.pagehelper.PageInterceptor;
import com.hp.sh.expv3.bb.extension.util.SqlSessionFactoryUtil;
import com.hp.sh.expv3.component.dbshard.ExShardingBuilder;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;


/**
 * @author BaiLiJun  on 2020/5/8
 */
//@Configuration
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

    @Lazy
    @Bean(name = "masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari.primary")
    public HikariDataSource masterDataSource() {
        HikariDataSource ds = new HikariDataSource();
        return ds;
    }

    @Lazy
    @Primary
    @Bean("shardingDataSource")
    public DataSource shardingDataSource(@Qualifier("masterDataSource") DataSource primaryDataSource) throws SQLException {
        // 配置分片规则
        ExShardingBuilder builder = new ExShardingBuilder();
        builder.setDataSourceList(Arrays.asList(primaryDataSource));
        builder.addAssetSubTableName("bb_account_record");
        builder.addSymbolSubTableName("bb_order_history");
        builder.addSymbolSubTableName("bb_order_trade");
        return builder.build();
    }

}
