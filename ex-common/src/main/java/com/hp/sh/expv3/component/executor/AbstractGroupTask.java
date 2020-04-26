package com.hp.sh.expv3.component.executor;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitee.hupadev.commons.executor.orderly.GroupTask;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;

public abstract class AbstractGroupTask implements GroupTask{
	
    private static final Logger logger = LoggerFactory.getLogger(AbstractGroupTask.class);

	@Override
	public void run() {
		try{
			if(logger.isDebugEnabled()){
				long time = System.currentTimeMillis();
				this.doRun();
				time = System.currentTimeMillis() - time;
				logger.debug("任务执行完成,{},{}", time, this.toString());
			}else{
				this.doRun();
			}
		}catch(ExException e){
			Throwable cause = ExceptionUtils.getRootCause(e);
			logger.error(e.toString(), cause);
		}catch(ExSysException e){
			Throwable cause = ExceptionUtils.getRootCause(e);
			logger.error(e.toString(), cause);
		}catch(Exception e){
			Throwable cause = ExceptionUtils.getRootCause(e);
			logger.error("未知捕获"+cause.getMessage(), e);
		}
	}

	public abstract void doRun();

}
