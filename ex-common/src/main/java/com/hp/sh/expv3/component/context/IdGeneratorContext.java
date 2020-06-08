package com.hp.sh.expv3.component.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.component.id.ZwIdGenerator;

@Component
public class IdGeneratorContext implements ApplicationContextAware{
	private ApplicationContext ctx;
	
	private static ZwIdGenerator idGenerator;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}
	
	@Autowired(required=false)
	public void setIdGenerator(ZwIdGenerator idGenerator) {
		IdGeneratorContext.idGenerator = idGenerator;
	}

	public static Long getSnowIdTime(String className, Long id){
		Long time = idGenerator.getTimeStamp(className, id);
		return time;
	}
	
	public static long getSnowIdTime(Long id){
		long time = idGenerator.getTimeStamp(id);
		return time;
	}

}
