package com.hp.sh.expv3.bb.job;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class BBMatchedHandleThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(BBMatchedHandleThread.class);

	@Autowired
	private BBMatchedHandler matchedHandler;

	private final Object lock = new Object();
	
	
	@Scheduled(cron = "0 0/10 * * * ?")
	public void timer() {
		this.trigger();
	}
	
	public void run(){
		while(true){
			try{
				matchedHandler.handlePending();
			}catch(Exception e){
				logger.error(e.getMessage(), e);
			}
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
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
