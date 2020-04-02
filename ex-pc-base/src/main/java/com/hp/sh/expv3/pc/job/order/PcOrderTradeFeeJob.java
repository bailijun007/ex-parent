package com.hp.sh.expv3.pc.job.order;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;
import com.hp.sh.expv3.pc.module.order.service.PcOrderQueryService;
import com.hp.sh.expv3.pc.module.position.service.PcTradeService;
import com.hp.sh.expv3.utils.DbDateUtils;

@Component
public class PcOrderTradeFeeJob {
    private static final Logger logger = LoggerFactory.getLogger(PcOrderTradeFeeJob.class);

	@Autowired
	private PcOrderQueryService orderQueryService;
	@Autowired
	private PcTradeService tradeService;
	
//	@Scheduled(cron = "0 * * * * ?")
	public void handleJob() {
		Long now = DbDateUtils.now();
		Long startTime = now-1000*3600*20000;
		Page page = new Page(1, 100, 1000L);
		while(true){
			List<PcOrderTrade> list = orderQueryService.querySynchFee(page, startTime);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(PcOrderTrade orderTrade : list){
				handleOrderTrade(orderTrade);
				
			}
			
		}
	}

	private void handleOrderTrade(PcOrderTrade orderTrade) {
		tradeService.synchCollector(orderTrade);
		tradeService.setSynchStatus(orderTrade);
	}
	
}
