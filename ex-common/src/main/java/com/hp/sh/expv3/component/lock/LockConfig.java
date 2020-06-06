package com.hp.sh.expv3.component.lock;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.hp.sh.expv3.component.lock.impl.ExRedissonClient;
import com.hp.sh.expv3.component.lock.impl.RedissonDistributedLocker;

@Configuration
public class LockConfig {

    @Value("${redisson.lock.module:}")
    private String modulePrefix;
    
    @Value("${lock.db.use.pessimistic:false}")
    private Boolean usePessimisticLock;
    
    @Autowired
    private ExRedissonClient redissonClient;
    
    @Value("${lock.redisson.use:true}")
    private Boolean useRedissonLock;
	
    @Lazy
    @Bean("redissonLocker")
    public RedissonDistributedLocker redissonLocker() throws IOException{
    	if(!useRedissonLock){
    		return null;
    	}
    	RedissonDistributedLocker rdLocker = new RedissonDistributedLocker();
    	rdLocker.setRedissonClient(redissonClient.getRedisson());
    	rdLocker.setModulePrefix(modulePrefix);
    	return rdLocker;
    }

	public Boolean usePessimisticLock() {
		return usePessimisticLock;
	}
}
