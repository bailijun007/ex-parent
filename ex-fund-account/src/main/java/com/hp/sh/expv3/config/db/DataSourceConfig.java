package com.hp.sh.expv3.config.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.HintShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.hp.sh.expv3.config.db.shard.DbHintShardingAlgorithm;
import com.hp.sh.expv3.config.db.shard.DbShardingAlgorithm;
import com.hp.sh.expv3.config.db.shard.DbShardingAlgorithm2;
import com.hp.sh.expv3.config.db.shard.TableShardingByDate;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@PropertySource({ "db2.properties" })
public class DataSourceConfig {

	@Order(1)
	@Bean("primaryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.hikari.primary")
	public DataSource primaryDataSource() {
		HikariDataSource ds = new HikariDataSource();
		return ds;
	}

	@Order(2)
	@Bean("secondDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.hikari.second")
	public DataSource secondDataSource() {
		HikariDataSource ds = new HikariDataSource();
		return ds;
	}
	
	
	@Primary
	@Order(3)
	@Bean("shardingDataSource")
	public DataSource shardingDataSource(@Qualifier("primaryDataSource") DataSource primaryDataSource, List<DataSource> dsList) throws SQLException {
		// 配置分片规则
		FunShardingBuilder builder = new FunShardingBuilder();
		builder.setDsList(dsList);
		builder.addTable("fund_account");
		builder.addTable("fund_account_record");
		builder.addTable("fund_transfer");
		builder.addTable("deposit_addr");
		builder.addTable("deposit_record");
		builder.addTable("withdrawal_addr");
		builder.addTable("withdrawal_record");
		return builder.build();
	}

}
