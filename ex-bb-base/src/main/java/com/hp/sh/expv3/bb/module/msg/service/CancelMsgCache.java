
package com.hp.sh.expv3.bb.module.msg.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.mq.listen.mq.MatchMqConsumer4Persist;

/**
 * 
 * @author wangjg
 *
 */
@Component
public class CancelMsgCache{
	private static final Logger logger = LoggerFactory.getLogger(CancelMsgCache.class);
	
	private static final String prefix = "cache:bb:msg:cancel:";
	
    @Resource(name = "templateDB0")
    private StringRedisTemplate template;

    public boolean existCancelMsg(Long userId, String tags, String keys){
    	try{
			String key = prefix + userId + "-" + tags + "-" + keys;
			ValueOperations<String, String> ops = template.opsForValue();
			String val = ops.get(key);
			
			if("Y".equals(val)){
				return true;
			}else{
				return false;
			}
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
    		return false;
    	}
	}

    public void cacheCancelMsg(Long userId, String tags, String keys){
    	try{
    		String key = prefix + userId + "-" + tags + "-" + keys;
    		ValueOperations<String, String> ops = template.opsForValue();
    		ops.set(key, "Y", 300, TimeUnit.SECONDS);
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
    	}
	}
	
}
