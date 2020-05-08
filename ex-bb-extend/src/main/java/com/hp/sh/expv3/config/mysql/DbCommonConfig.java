package com.hp.sh.expv3.config.mysql;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.session.LocalCacheScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Properties;


@Configuration
public class DbCommonConfig {
    @Value("${mybatis.configuration.cache-enabled}")
    private Boolean cacheEnabled;

    @Value("${mybatis.configuration.local-cache-scope}")
    private String localCacheSocpe;

    @Value("${page.helper.helper-dialect}")
    private String helperDialect;

    @Value("${page.helper.reasonable}")
    private String reasonable;

    @Value("${page.helper.support-methods-arguments}")
    private String supportMethodsArguments;

    private static final String LOCAL_CACHE_SCOPE_SESSION = "session";
    private static final String PAGE_PROP_HELPER_DIALECT = "helperDialect";
    private static final String PAGE_PROP_REASONABLE = "reasonable";
    private static final String PAGE_PROP_SUPPORT_METHODS_ARGUMENTS = "supportMethodsArguments";
    private static final String PAGE_PROP_PARAMS = "params";
    private static final String PAGE_HELPER_PARAMS_VALUE = "count=countSql";




    @Bean(name = "mybatisConfig")
    @Scope("prototype")
    public org.apache.ibatis.session.Configuration  mybatisConfig(){
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setCacheEnabled(cacheEnabled);
        if(LOCAL_CACHE_SCOPE_SESSION.equals(localCacheSocpe)){
            configuration.setLocalCacheScope(LocalCacheScope.SESSION);
        }else {
            configuration.setLocalCacheScope(LocalCacheScope.STATEMENT);
        }
        return configuration;
    }

    @Bean
    @Scope("prototype")
    public PageInterceptor pageInterceptor(){
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties p = new Properties();
        p.setProperty(PAGE_PROP_HELPER_DIALECT, helperDialect);
        p.setProperty(PAGE_PROP_REASONABLE, reasonable);
        p.setProperty(PAGE_PROP_SUPPORT_METHODS_ARGUMENTS, supportMethodsArguments);
        p.setProperty(PAGE_PROP_PARAMS,PAGE_HELPER_PARAMS_VALUE);
        pageInterceptor.setProperties(p);
        return pageInterceptor;
    }

}
