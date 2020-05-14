package com.hp.sh.expv3.config.redis.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitee.hupadev.commons.cache.JsonCacheSerializer;
import com.gitee.hupadev.commons.cache.MsgListener;
import com.gitee.hupadev.commons.cache.RedisPool;
import com.gitee.hupadev.commons.cache.RedisSubscriber;

public class RedisSubscriberTest extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(RedisSubscriberTest.class);
	
	private String channel;
	
	private RedisSubscriber rs;

    public RedisSubscriberTest(String channel, RedisPool redisPool) {
		this.channel = channel;
		this.rs = new RedisSubscriber(redisPool);
	}

	public String getChannel() {
		return channel;
	}

	public void run(){
		try{
			rs.setCacheSerializer(new JsonCacheSerializer());
			rs.setMsgListener(new MsgListener(){

				@Override
				public void onMessage(Object message) {
					logger.info("收到订阅信息 : {},{}", channel, message);
				}

			});
			rs.setChannel(channel);
			rs.subscribe();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void shutdown(){
		rs.shutdown();
	}
    
}
