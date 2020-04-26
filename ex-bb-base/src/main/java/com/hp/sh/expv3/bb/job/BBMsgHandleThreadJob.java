package com.hp.sh.expv3.bb.job;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Order(999)
@Component
public class BBMsgHandleThreadJob extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(BBMsgHandleThreadJob.class);

	@Autowired
	private BBMsgHandler matchedHandler;

	private final Object lock = new Object();
	
	
	@Scheduled(cron = "0 * * * * ?")
	public void timer() {
		this.trigger();
	}
	
	public void run(){
		while(true){
			try{
				matchedHandler.handlePending();
			}catch(Exception e){
				logger.error("分派消息失败：{}", e.getMessage(), e);
				try {
					Thread.sleep(1000*10);
				} catch (InterruptedException e1) {
					logger.error(e.getMessage(), e);
				}
			}
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	public void trigger(){
        synchronized (lock) {
            lock.notifyAll();
        }
	}
	
	@PostConstruct
	void begin(){
		this.start();
	}

}
