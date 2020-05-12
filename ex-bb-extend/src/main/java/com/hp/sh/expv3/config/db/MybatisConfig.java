package com.hp.sh.expv3.config.db;

import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gitee.hupadev.commons.mybatis.PageInterceptor;

@Configuration
@MapperScan({"com.hp.sh.expv3.bb.**.dao", "com.hp.sh.expv3.bb.*.mapper"})
public class MybatisConfig implements ApplicationContextAware {
	private ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}

	@Bean
	ConfigurationCustomizer mybatisConfigurationCustomizer() {
		return new ConfigurationCustomizer() {
			@Override
			public void customize(org.apache.ibatis.session.Configuration config) {
				config.setMapUnderscoreToCamelCase(true);
				config.addInterceptor(new PageInterceptor());
				config.setLogImpl(Slf4jImpl.class);
			}
		};
	}

}
