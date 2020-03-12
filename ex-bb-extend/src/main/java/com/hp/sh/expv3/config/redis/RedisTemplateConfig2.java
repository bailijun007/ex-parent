//package com.hp.sh.expv3.config.redis;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisPassword;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//@Configuration
//public class RedisTemplateConfig2 {
//
//    @Bean("klineCf5")
//    public RedisConnectionFactory redisConnectionFactory0() {
//        LettuceConnectionFactory cf = new LettuceConnectionFactory(standaloneConfiguration(5));
//        return cf;
//    }
//
//    @Value("${kline.redis.host}")
//    private String hostName;
//    @Value("${kline.redis.port}")
//    private int port;
//    @Value("${kline.redis.password}")
//    private String password;
//
//    private RedisStandaloneConfiguration standaloneConfiguration(int dataBase) {
//        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
//        conf.setHostName(hostName);
//        conf.setPort(port);
//        conf.setPassword(RedisPassword.of(password));
//        conf.setDatabase(dataBase);
//        return conf;
//    }
//
//    @Bean(name = "klineTemplateDB5")
//    public StringRedisTemplate tradeTemplate5(@Qualifier("klineCf5") RedisConnectionFactory redisConnectionFactory) {
//        StringRedisTemplate template = new StringRedisTemplate();
//        template.setConnectionFactory(redisConnectionFactory);
//        return template;
//    }
//
//}
