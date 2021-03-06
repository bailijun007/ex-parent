package com.hp.sh.expv3.config.db;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.hp.sh.expv3.component.dbshard.ExShardingBuilder;
import com.hp.sh.expv3.component.dbshard.TableInfoCache;
import com.hp.sh.expv3.component.dbshard.impl.TradeId2DateShard;
import com.hp.sh.expv3.component.id.utils.IdBitSetting;
import com.hp.sh.expv3.component.id.utils.SnowflakeIdWorker;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {
	
	@Autowired(required=false)
	private TableInfoCache tableInfoCache;

	@Order(1)
	@Bean("primaryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.hikari.primary")
	public DataSource primaryDataSource() {
		HikariDataSource ds = new HikariDataSource();
		return ds;
	}
	
	@Bean("tradeSnowflakeIdWorker")
	public SnowflakeIdWorker tradeSnowflakeIdWorker(){
		return new SnowflakeIdWorker(IdBitSetting.dataCenterBits, IdBitSetting.serverBits, IdBitSetting.idTypeBits, IdBitSetting.sequenceBits);
	}
	
//	@Primary
//	@Order(3)
//	@Bean("shardingDataSource")
//	public DataSource shardingDataSource(@Qualifier("primaryDataSource") DataSource primaryDataSource, List<DataSource> dsList, TradeId2DateShard tid) throws SQLException {
//		// 配置分片规则
//		ExShardingBuilder builder = new ExShardingBuilder();
//		builder.setDataSourceList(dsList);
//		if(this.tableInfoCache!=null){
//			builder.setTableInfoCache(this.tableInfoCache);
//		}
//		builder.addAssetSubTable("bb_account", "id", "created");
//		return builder.build();
//	}
	
}
