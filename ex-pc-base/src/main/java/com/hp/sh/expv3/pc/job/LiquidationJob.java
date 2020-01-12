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
import com.hp.sh.expv3.pc.mq.MatchMqSender;
import com.hp.sh.expv3.pc.mq.liq.msg.LiqLockMsg;
import com.hp.sh.expv3.pc.vo.response.MarkPriceVo;

@Component
public class LiquidationJob {
    private static final Logger logger = LoggerFactory.getLogger(LiquidationJob.class);
    
    @Autowired
    private PcPositionMarginService pcPositionMarginService;
    
    @Autowired
    private PcLiqService pcLiqService;

    @Autowired
    private MatchMqSender liqMqSender;
    
	/**
	 * 
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void handle() {
		Page page = new Page(1, 100, 1000L);
		while(true){
			List<PcPosition> list = pcPositionMarginService.queryActivePosList(page, null, null, null);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(PcPosition pos : list){
				LiqHandleResult liqResult = pcLiqService.handleLiq(pos);
				if(liqResult.isTrigger()){
					this.sendLiqMsg(pos, liqResult);
				}
			}
			
			page.setPageNo(page.getPageNo()+1);
		}
	}
	
	private void sendLiqMsg(PcPosition pos, LiqHandleResult liqResut){
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
