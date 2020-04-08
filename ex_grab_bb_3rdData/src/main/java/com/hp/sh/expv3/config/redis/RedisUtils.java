package com.hp.sh.expv3.config.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author BaiLiJun  on 2020/4/8
 */
public class RedisUtils {
    public static JedisPool pool;

    public static JedisPool open(String ip,int port,int maxTotal,int maxIdle,int timeout,String password){
        if(pool==null){
            //创建JedisPoolConfig 给config设置连接池参数，使用config创建JedisPool
            JedisPoolConfig config=new JedisPoolConfig();
            //设置最大线程数 一个线程就是一个jedis
            config.setMaxTotal(maxTotal);
            //设置最大空闲数
            config.setMaxIdle(maxIdle);
            //设置检查项为true，表示从线程池拿出来的对象一定是检查可用
            config.setTestOnBorrow(true);
            pool=new JedisPool(config,ip,port,timeout,password);
        }
        return pool;
    }
    //关闭pool对象
    public static void close(){
        if(pool!=null){
            pool.close();
        }
    }

}
