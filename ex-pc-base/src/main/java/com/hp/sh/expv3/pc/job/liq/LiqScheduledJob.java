package com.hp.sh.expv3.pc.job.liq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.dev.LimitTimeHandle;

@Component
public class LiqScheduledJob {
    private static final Logger logger = LoggerFactory.getLogger(LiqScheduledJob.class);
    
    @Autowired
    private LiquidationHandler liquidationHandler;
    
	@Scheduled(cron = "${cron.liq.check}")
	public void checkLiqOrder() {
		liquidationHandler.checkLiqOrder();
	}
	
	/**
	 * 创建强平委托
	 */
	@LimitTimeHandle
//	@Scheduled(cron = "${cron.liq.handle}")
	public void hanleLiqOrder(){
		liquidationHandler.hanleLiqOrder();
	}

}
