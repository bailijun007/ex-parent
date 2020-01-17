package com.hp.sh.expv3.config.db;

import javax.sql.DataSource;

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
	
//	@Order(1)
//	public String  test(){
//		
//		return null;
//	}

	@Primary
	@Order(1)
	@Bean("primaryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.hikari.primary")
	public DataSource primaryDataSource() {
		HikariDataSource ds = new HikariDataSource();
		return ds;
	}

}
