package com.hp.sh.expv3.config.redis.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.gitee.hupadev.commons.cache.RedisPool;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.component.vo.PcContractVO;

@Configuration
@ConditionalOnProperty(name="redis.subscriber.test", havingValue="true")
public class RedisSubscriberTestStarter {
    private static final Logger logger = LoggerFactory.getLogger(RedisSubscriberTestStarter.class);
    
	@Autowired
	private MetadataService metadataService;
	
	@Autowired
	@Qualifier("expRedisPool") 
	private RedisPool redisPool;

	private final Map<String,RedisSubscriberTest> subMap = new LinkedHashMap<String,RedisSubscriberTest>();
	
	@Scheduled(cron = "0 * * * * ?")
	@PostConstruct
	public void testRs(){
		List<PcContractVO> pcList = this.metadataService.getAllPcContract();
		
		Set<String> channels = new HashSet<String>();
		
		for(PcContractVO bbvo : pcList){
			channels.add("pc:account:"+bbvo.getAsset());
			channels.add("pc:order:"+bbvo.getAsset()+":"+bbvo.getSymbol());
			channels.add("pc:pos:"+bbvo.getAsset()+":"+bbvo.getSymbol());
			channels.add("pc:user:symbol:"+bbvo.getAsset()+":"+bbvo.getSymbol());
		}
		
		//del
		for(String channel : new ArrayList<String>(this.subMap.keySet())){
			RedisSubscriberTest sub = this.subMap.get(channel);
			if(!channels.contains(channel)){
				logger.info("关闭订阅. channel={}", channel);
				sub.shutdown();
				this.subMap.remove(channel);
			}
		}
		
		//add
		for(String channel : channels){
			if(!subMap.containsKey(channel)){
				logger.info("开启订阅. channel={}", channel);
				RedisSubscriberTest sub = new RedisSubscriberTest(channel, redisPool);
				sub.start();
				subMap.put(channel, sub);
			}
		}
	}
	
}