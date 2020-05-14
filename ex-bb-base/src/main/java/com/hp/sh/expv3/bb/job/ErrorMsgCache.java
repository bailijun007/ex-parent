package com.hp.sh.expv3.bb.job;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames="bbErrorMsg")
public class ErrorMsgCache {
    @CachePut(key="#orderId")
	public Long saveErrorMsgIdCache(Object orderId, Long msgId){
		return msgId;
	}

	@Cacheable(key="#orderId")
	public Long getErrorMsgIdCache(Object orderId){
		return null;
	}

	@CacheEvict(key="#orderId")
	public Object evictErrorMsgIdCache(Object orderId){
		return orderId;
	}
	
}
