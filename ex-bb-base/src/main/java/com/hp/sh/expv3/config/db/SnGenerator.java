package com.hp.sh.expv3.config.db;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.id.generator.SnowflakeSn;
import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecord;
import com.hp.sh.expv3.bb.module.collector.entity.BBCollectorAccountRecord;

@Component
public class SnGenerator {
	
	@Value("${id.generator.dataCenterId}")
	private int dataCenterId;
	
	@Value("${id.generator.serverId}")
	private int serverId;
	
	private final Map<Class, SnowflakeSn> idWorkerMap = new HashMap<Class, SnowflakeSn>();
	
	public SnGenerator() {
		
	}
	
	@PostConstruct
	public void setIdWorker(){
		idWorkerMap.put(BBAccountRecord.class, new SnowflakeSn(dataCenterId, serverId));
		idWorkerMap.put(BBCollectorAccountRecord.class, new SnowflakeSn(dataCenterId, serverId));
	}

	public String genSn(Object entity){
		SnowflakeSn gen = idWorkerMap.get(entity.getClass());
		return gen.nextSn();
	}
	
}
