package com.hp.sh.expv3.pc.job.liq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.dev.LimitTimeHandle;
import com.xxl.job.core.handler.annotation.XxlJob;

@Component
public class LiqXxlJob {
    private static final Logger logger = LoggerFactory.getLogger(LiqXxlJob.class);
    
    @Autowired
    private LiquidationService liquidationService;
    
    @XxlJob("checkLiqOrder")
	public void checkLiqOrder() {
		liquidationService.checkLiqOrder();
	}
	
	/**
	 * 创建强平委托
	 */
	@LimitTimeHandle
//	@XxlJob("hanleLiqOrder")
	public void hanleLiqOrder(){
		liquidationService.hanleLiqOrder();
	}

}
