package com.hp.sh.expv3.component.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OrderlyExecutor {
	
	private BlockingQueue<Runnable> queue ;
	
	private ExecutorService pool ;

	public OrderlyExecutor() {
		this(100);
	}

	public OrderlyExecutor(int queueSize) {
		this.queue = new LinkedBlockingQueue<Runnable>(queueSize);
		this.pool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, queue);
	}

	public void submit(Runnable task){
		pool.submit(task);
	}
	
	public int getQueueSize(){
		return this.queue.size();
	}
}
