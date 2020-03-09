package com.hp.sh.expv3.config.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitee.hupadev.commons.cache.JsonCacheSerializer;
import com.gitee.hupadev.commons.cache.MsgListener;
import com.gitee.hupadev.commons.cache.RedisPool;
import com.gitee.hupadev.commons.cache.RedisSubscriber;

public class RedisSubscriberTest extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(RedisSubscriberTest.class);
	
	private String channel;
	
	private RedisPool redisPool;

    public RedisSubscriberTest(String channel, RedisPool redisPool) {
		this.channel = channel;
		this.redisPool = redisPool;
	}

	public void run(){
		try{
			final RedisSubscriber rs = new RedisSubscriber(redisPool);
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
    
}
