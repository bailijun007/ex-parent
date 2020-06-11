
package com.hp.sh.expv3.bb.module.msg.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * 
 * @author wangjg
 *
 */
@Component
public class CancelMsgCache{
	
	private static final String prefix = "cache:bb:msg:cancel:";
	
    @Resource(name = "templateDB0")
    private StringRedisTemplate template;

    public boolean existCancelMsg(Long userId, String tags, String keys){
		String key = prefix + userId + "-" + tags + "-" + keys;
		ValueOperations<String, String> ops = template.opsForValue();
		String val = ops.get(key);
		
		if("Y".equals(val)){
			return true;
		}else{
			return false;
		}
	}

    public void cacheCancelMsg(Long userId, String tags, String keys){
		String key = prefix + userId + "-" + tags + "-" + keys;
		ValueOperations<String, String> ops = template.opsForValue();
		ops.set(key, "Y", 60, TimeUnit.SECONDS);
	}
	
}
