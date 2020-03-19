package com.hp.sh.expv3.pc.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.dev.LimitTimeHandle;
import com.hp.sh.expv3.pc.module.liq.entity.PcLiqRecord;
import com.hp.sh.expv3.pc.module.liq.entity.PcLiqStatus;
import com.hp.sh.expv3.pc.module.liq.service.PcLiqRecordService;
import com.hp.sh.expv3.pc.module.liq.service.PcLiqService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderQueryService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.service.PcPositionDataService;
import com.hp.sh.expv3.pc.module.position.vo.PosUID;
import com.hp.sh.expv3.pc.mq.MatchMqSender;
import com.hp.sh.expv3.pc.mq.liq.msg.LiqLockMsg;
import com.hp.sh.expv3.pc.vo.response.MarkPriceVo;
import com.hp.sh.expv3.utils.DbDateUtils;

@Component
public class LiquidationJob {
    private static final Logger logger = LoggerFactory.getLogger(LiquidationJob.class);
    
	@Autowired
	private PcPositionDataService positionDataService;
    
    @Autowired
    private PcLiqService pcLiqService;

    @Autowired
    private PcLiqRecordService liqRecordService;
    
	@Autowired
	private PcOrderQueryService orderQueryService;

    @Autowired
    private MatchMqSender liqMqSender;
    
    private Long startTime = DbDateUtils.now()-1000*3600;
    
	/**
	 * 
	 */
	@Scheduled(cron = "${cron.liq.check}")
	public void check() {
		Page page = new Page(1, 200, 1000L);
		while(true){
			List<PosUID> list = positionDataService.queryActivePosIdList(page, null, null, null);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			logger.warn("活动仓位:{}", list.size());
			for(PosUID pos : list){
				try{
					LiqHandleResult liqResult = pcLiqService.checkPosLiq(pos);
					if(liqResult.isTrigger()){
						logger.warn("触发强平:{}", pos);
						this.sendLiqMsg(liqResult);
					}
				}catch(Exception e){
					logger.error("检查强平错误:"+e.getMessage(), e);
				}
			}
			
			page.setPageNo(page.getPageNo()+1);
		}
	}
	
	/**
	 * 处理强平
	 */
	@CrossDB
	@LimitTimeHandle
	@Scheduled(cron = "${cron.liq.handle}")
	public void handle(){
		Page page = new Page(1, 50, 1000L);
		Long startId = null;
		while(true){
			List<PcLiqRecord> list = this.liqRecordService.queryPending(page, null, startTime, startId);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(PcLiqRecord record : list){
				try{
					handleOne(record);
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
				startId = record.getId();
			}
			
		}
		
	}
	
	private void handleOne(PcLiqRecord record) {
		if(record.getStatus()==PcLiqStatus.init){
			this.pcLiqService.createLiqOrder(record);
		}else if(record.getStatus()==PcLiqStatus.step1){
			
		}else if(record.getStatus()==PcLiqStatus.step2){
			
		}else if(record.getStatus()==PcLiqStatus.step3){
			
		}
	}

	private void sendLiqMsg(LiqHandleResult liqResut){
		PcPosition pos = liqResut.getPcPosition();
		MarkPriceVo markPriceVo = liqResut.getMarkPriceVo();
		//发送强平消息
		LiqLockMsg lockMsg = new LiqLockMsg();
		lockMsg.setAccountId(pos.getUserId());
		lockMsg.setAsset(pos.getAsset());
		lockMsg.setSymbol(pos.getSymbol());
		lockMsg.setLongFlag(pos.getLongFlag());
		lockMsg.setPosId(pos.getId());
		lockMsg.setLiqPrice(liqResut.getLiqPrice());
		lockMsg.setLiqMarkTime(markPriceVo.getTime());
		lockMsg.setLiqMarkPrice(markPriceVo.getMarkPrice());
		this.liqMqSender.sendLiqLockMsg(lockMsg);
	}


}
