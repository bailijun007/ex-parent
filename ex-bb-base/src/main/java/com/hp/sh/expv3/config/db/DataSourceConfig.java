package com.hp.sh.expv3.config.db;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

	@Order(1)
	@Bean("primaryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.hikari.primary")
	public DataSource primaryDataSource() {
		HikariDataSource ds = new HikariDataSource();
		return ds;
	}
	
	@Primary
	@Order(3)
	@Bean("shardingDataSource")
	public DataSource shardingDataSource(@Qualifier("primaryDataSource") DataSource primaryDataSource, List<DataSource> dsList) throws SQLException {
		// 配置分片规则
		BBShardingBuilder builder = new BBShardingBuilder();
		builder.setDataSourceList(dsList);
		builder.addAssetSubTableName("bb_account_record");
		builder.addSymbolSubTableName("bb_order");
		builder.addSymbolSubTableName("bb_order_trade");
		return builder.build();
	}
	
}
