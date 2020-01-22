package com.hp.sh.expv3.config.db;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    final Logger logger = LoggerFactory.getLogger(getClass());

	@Primary
	@Order(1)
	@Bean("primaryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.hikari.primary")
	public DataSource primaryDataSource() {
		HikariDataSource ds = new HikariDataSource();
		return ds;
	}

	@Value("${spring.datasource.hikari.primary.jdbcUrl}")
	private String jdbcUrl;
	@Value("${spring.datasource.hikari.primary.username}")
	private String username;
	@Value("${bys.client.secret}")
	private String bysSecret;
	
	@PostConstruct
	public void printdbinfo(){
		logger.error("url={},username={}, bysSecret={}", jdbcUrl, username, bysSecret);
	}
}
