package com.hp.sh.expv3.pc.job.msghandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgShardHandlerThread extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(MsgShardHandlerThread.class);

	private final MsgShardHandler msgShardHandler;

	private final Object lock = new Object();
	
	private final Long shardId;
	
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
	
}
