package com.hp.sh.expv3.pc.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.service.PcLiqService;
import com.hp.sh.expv3.pc.module.position.service.PcPositionMarginService;
import com.hp.sh.expv3.pc.mq.liq.LiqMqSender;

@Component
public class LiquidationJob {
    private static final Logger logger = LoggerFactory.getLogger(LiquidationJob.class);
    
    @Autowired
    private PcPositionMarginService pcPositionMarginService;
    
    @Autowired
    private PcLiqService pcLiqService;
    
	/**
	 * 
	 */
	@Scheduled(cron = "0/10 * * * * ?")
	public void handle() {
		Page page = new Page(1, 100, 1000L);
		while(true){
			List<PcPosition> list = pcPositionMarginService.queryActivePosList(page, null, null, null);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(PcPosition pos : list){
				pcLiqService.handleLiq(pos);
			}
			
			page.setPageNo(page.getPageNo()+1);
		}
	}




}
