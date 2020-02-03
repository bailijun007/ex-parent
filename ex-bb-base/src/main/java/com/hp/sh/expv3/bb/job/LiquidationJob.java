package com.hp.sh.expv3.bb.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.module.position.entity.PcPosition;
import com.hp.sh.expv3.bb.module.position.service.PcLiqService;
import com.hp.sh.expv3.bb.module.position.service.PcPositionDataService;
import com.hp.sh.expv3.bb.module.position.vo.PosUID;
import com.hp.sh.expv3.bb.mq.MatchMqSender;
import com.hp.sh.expv3.bb.mq.liq.msg.LiqLockMsg;
import com.hp.sh.expv3.bb.vo.response.MarkPriceVo;

@Component
public class LiquidationJob {
    private static final Logger logger = LoggerFactory.getLogger(LiquidationJob.class);
    
	@Autowired
	private PcPositionDataService positionDataService;
    
    @Autowired
    private PcLiqService pcLiqService;

    @Autowired
    private MatchMqSender liqMqSender;
    
	/**
	 * 
	 */
	@Scheduled(cron = "${cron.liq}")
	public void handle() {
		Page page = new Page(1, 200, 1000L);
		while(true){
			List<PosUID> list = positionDataService.queryActivePosIdList(page, null, null, null);
			
			if(list==null || list.isEmpty()){
				break;
			}
			logger.warn("活动仓位:{}", list.size());
			for(PosUID pos : list){
				LiqHandleResult liqResult = pcLiqService.checkPosLiq(pos);
				if(liqResult.isTrigger()){
					logger.warn("触发强平:{}", pos);
					this.sendLiqMsg(liqResult);
				}
			}
			
			page.setPageNo(page.getPageNo()+1);
		}
	}
	
	private void sendLiqMsg(LiqHandleResult liqResut){
		PcPosition pos = liqResut.getPcPosition();
		MarkPriceVo markPriceVo = liqResut.getMarkPriceVo();
		//发送强平消息
		LiqLockMsg lockMsg = new LiqLockMsg();
		lockMsg.setAccountId(pos.getUserId());
		lockMsg.setAsset(pos.getAsset());
		lockMsg.setLiqMarkPrice(markPriceVo.getMarkPrice());
		lockMsg.setLiqMarkTime(markPriceVo.getTime());
		lockMsg.setLiqPrice(liqResut.getLiqPrice());
		lockMsg.setLongFlag(pos.getLongFlag());
		lockMsg.setPosId(pos.getId());
		lockMsg.setSymbol(pos.getSymbol());
		this.liqMqSender.sendLiqLockMsg(lockMsg);
	}


}
