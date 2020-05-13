package com.hp.sh.expv3.bb.job;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ErrorMsgCache {
    @CachePut(cacheNames="errorMsg", key="#orderId")
	public Long saveErrorMsgIdCache(Object orderId, Long msgId){
		return msgId;
	}

	@Cacheable(cacheNames="errorMsg", key="#orderId")
	public Long getErrorMsgIdCache(Object orderId){
		return null;
	}

	@CacheEvict(cacheNames="errorMsg", key="#orderId")
	public Object evictErrorMsgIdCache(Object orderId){
		return orderId;
	}
	
}
