package com.hp.sh.expv3.config.db;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.hp.sh.expv3.component.dbshard.ExShardingBuilder;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

//    @Order(1)
//    @Bean("primaryDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.hikari.primary")
//    public DataSource primaryDataSource() {
//        return DataSourceBuilder.create().type(HikariDataSource.class).build();
//    }

    @Primary
    @Order(2)
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
