package com.hp.sh.expv3.bb.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hp.sh.expv3.component.lock.impl.RedissonDistributedLocker;

public class MsgShardHandlerThread extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(MsgShardHandlerThread.class);

	private final MsgShardHandler msgShardHandler;

	private final Object notifyLock = new Object();
	
	private final Long shardId;
	
	@Autowired
	private RedissonDistributedLocker distributedLocker;
	
	public MsgShardHandlerThread(MsgShardHandler msgShardHandler, Long shardId) {
		super("MsgShardHandlerThread-"+shardId);
		this.msgShardHandler = msgShardHandler;
		this.shardId = shardId;
	}

	public void run(){
		while(true){
			try{
				msgShardHandler.handlePending(shardId);
			}catch(Exception e){
				logger.error("分派消息失败：{}", e.getMessage(), e);
				try {
					Thread.sleep(1000*10);
				} catch (InterruptedException e1) {
					logger.error(e.getMessage(), e);
				}
			}
			synchronized (notifyLock) {
				try {
					notifyLock.wait();
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	public void trigger(){
        synchronized (notifyLock) {
            notifyLock.notifyAll();
        }
	}
	
}
