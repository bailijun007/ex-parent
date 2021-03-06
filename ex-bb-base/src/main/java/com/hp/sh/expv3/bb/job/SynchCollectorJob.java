package com.hp.sh.expv3.bb.job;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.component.MetadataService;
import com.hp.sh.expv3.bb.component.vo.BBSymbolVO;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.module.order.service.BBOrderQueryService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.dev.LimitTimeHandle;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;

@Component
@ConditionalOnProperty(name="synch.collector", havingValue="true")
public class SynchCollectorJob {
    private static final Logger logger = LoggerFactory.getLogger(SynchCollectorJob.class);

	@Autowired
	private BBOrderQueryService orderQueryService;
	
	@Autowired
	private BBTradeService tradeService;
	
	@Autowired
	private MetadataService metadataService;
	
	@XxlJob("bbSynchCollector")
    public ReturnT<String> xxlJobHandler(String param) throws Exception {
		Long startTime = DbDateUtils.now()-1000*60*2;
		if(StringUtils.isNotBlank(param)){
			startTime = Long.parseLong(param);
		}
		logger.info("XXL同步手续费...，{}", startTime);
    	this.handleJob(startTime, 100);
    	logger.info("XXL同步手续费结束。");
        return ReturnT.SUCCESS;
    }
	
	@Scheduled(cron = "0 0/1 * * * ?")
	public void scheduledHandleJob() {
		Long startTime = DbDateUtils.now()-1000*60*5;
		logger.info("SCHEDULED同步手续费...，{}", startTime);
		this.handleJob(startTime, 100);
		logger.info("SCHEDULED同步手续费结束。");
	}
	
	@LimitTimeHandle
	private void handleJob(Long startTime, Integer pageSize) {
		Page page = new Page(1, pageSize, 1000L);
		while(true){
			List<BBSymbolVO> symbolList = metadataService.getAllBBSymbol();
			for(BBSymbolVO vo : symbolList){
				List<BBOrderTrade> list = orderQueryService.querySynchFee(page, vo.getAsset(), vo.getSymbol(), startTime);
				if(list==null || list.isEmpty()){
					break;
				}
				for(BBOrderTrade orderTrade : list){
					try{
						handleOrderTrade(orderTrade);
					}catch(Exception e){
						logger.error(e.getMessage(), e);
					}
				}
			}
			break;
		}
	}

	private void handleOrderTrade(BBOrderTrade orderTrade) {
		tradeService.synchCollector(orderTrade);
		tradeService.setSynchStatus(orderTrade);
	}
	
}
