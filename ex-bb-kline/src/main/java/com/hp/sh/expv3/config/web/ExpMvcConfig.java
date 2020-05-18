package com.hp.sh.expv3.config.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitee.hupadev.base.spring.plugin.HpLocaleResolver;
import com.hp.sh.expv3.commons.web.LogFilter;

/**
 * @author wangjg
 *
 */
@ServletComponentScan(basePackages = {"com.hp.sh.expv3"})
@Configuration
@ComponentScan
public class ExpMvcConfig implements WebMvcConfigurer {
	private static final Logger logger = LoggerFactory.getLogger(ExpMvcConfig.class);

	@Autowired
	public ObjectMapper objectMapper;

	@Value("${spring.application.name}")
	private String appName;

	// 添加拦截器
	public void addInterceptors(InterceptorRegistry registry) {

	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	}

	@Bean
    public LocaleResolver localeResolver(){
        return new HpLocaleResolver();
    }

    @Bean
    public FilterRegistrationBean<LogFilter> myLogFilter(){
        FilterRegistrationBean<LogFilter> bean = new FilterRegistrationBean<LogFilter>();
        bean.setFilter(new LogFilter(appName));
        bean.addUrlPatterns("/api/*");
        return bean;
    }
}