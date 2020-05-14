package com.hp.sh.expv3.pc.job.liq;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.dev.LimitTimeHandle;
import com.hp.sh.expv3.dev.TransWarn;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.module.liq.entity.LiqRecordStatus;
import com.hp.sh.expv3.pc.module.liq.entity.PcLiqRecord;
import com.hp.sh.expv3.pc.module.liq.service.PcLiqRecordService;
import com.hp.sh.expv3.pc.module.liq.service.PcLiqService;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.service.PcOrderQueryService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.service.PcPositionDataService;
import com.hp.sh.expv3.pc.module.position.service.PcTradeService;
import com.hp.sh.expv3.pc.module.position.vo.PosUID;
import com.hp.sh.expv3.pc.module.riskfund.service.PcRiskfundCoreService;
import com.hp.sh.expv3.pc.mq.MatchMqSender;
import com.hp.sh.expv3.pc.mq.consumer.msg.liq.LiqLockMsg;
import com.hp.sh.expv3.pc.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.strategy.vo.LiqHandleResult;
import com.hp.sh.expv3.pc.vo.request.RiskFundRequest;
import com.hp.sh.expv3.pc.vo.response.MarkPriceVo;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.BigUtils;

@Component
public class LiquidationService {
    private static final Logger logger = LoggerFactory.getLogger(LiquidationService.class);
    
	@Autowired
	private PcPositionDataService positionDataService;
    
    @Autowired
    private PcLiqService pcLiqService;

    @Autowired
    private PcLiqRecordService liqRecordService;
    
	@Autowired
	private PcOrderQueryService orderQueryService;
    
	@Autowired
	private PcOrderService orderService;

    @Autowired
    private PcRiskfundCoreService riskfundCoreService;
    
    @Autowired
    private PcTradeService tradeService; 

    @Autowired
    private MatchMqSender liqMqSender;

    @Autowired
	private LiquidationService self;
    
    
	@Scheduled(cron = "${cron.liq.check}")
	public void checkLiqOrder() {
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
	 * 创建强平委托
	 */
	@LimitTimeHandle
//	@Scheduled(cron = "${cron.liq.handle}")
	public void hanleLiqOrder(){
		Long startTime = DbDateUtils.now()-1000*3600;
		this.synchLiqFund(startTime);
		this.createBankruptOrder(startTime);
		self.cutPosition(startTime);
	}

	@TransWarn
	private void createBankruptOrder(Long startTime){
		Page page = new Page(1, 50, 1000L);
		Long startId = null;
		while(true){
			List<PcLiqRecord> list = this.liqRecordService.queryPending(page, null, startTime, startId);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(PcLiqRecord record : list){
				try{
					PcOrder order = this.pcLiqService.createLiqOrder(record);
					this.liqMqSender.sendPendingNew(order);
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
				startId = record.getId();
			}
			
		}
		
	}

	@CrossDB("queryPendingFund")
	@TransWarn
	private void synchLiqFund(Long startTime){
		Page page = new Page(1, 50, 1000L);
		Long startId = null;
		List<PcLiqRecord> list = null;
		while(list!=null && !list.isEmpty()){
			list = this.liqRecordService.queryPendingFund(page, null, startTime, startId);
			for(PcLiqRecord record : list){
				try{
					PcOrder order = this.pcLiqService.createLiqOrder(record);
					this.liqMqSender.sendPendingNew(order);
					
					RiskFundRequest request = new RiskFundRequest();
					request.setAsset(record.getAsset());
					request.setAmount(record.getPnl());
					request.setTradeNo(""+record.getId());
					request.setTradeType(0);
					request.setUserId(record.getUserId());
					request.setRemark("强平盈余");
					this.riskfundCoreService.add(request);
					
					record.setStatus(LiqRecordStatus.FINISHED);
					Long now = DbDateUtils.now();
					record.setModified(now);
					this.liqRecordService.update(record);
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
				startId = record.getId();
			}
			
		}
		
	}

	/*
	 * 减仓
	 */
	@TransWarn
	@Transactional(rollbackFor=Exception.class)
	public void cutPosition(Long startTime){
		Page page = new Page(1, 50, 1000L);
		Long startId = null;
		while(true){
			List<PcOrder> list = this.orderQueryService.queryLiqCutOrders(page, startTime);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(PcOrder order : list){
				Long now = DbDateUtils.now();
				try{
					//自动减仓
					BigDecimal cv = order.getCancelVolume();
					while(BigUtils.gtZero(cv)){
						int longFlag = IntBool.isTrue(order.getLongFlag())?OrderFlag.TYPE_SHORT:OrderFlag.TYPE_LONG;
						PcPosition pos = this.positionDataService.getCutPos(order.getAsset(), order.getSymbol(), longFlag);
						BigDecimal number = null;
						if(BigUtils.ge(pos.getVolume(), cv)){
							number = cv;
						}else{
							number = pos.getVolume();
						}
						PcOrder cutOrder = orderService.createLiqOrder(pos.getUserId(), "LQ-"+pos.getId(), pos.getAsset(), pos.getSymbol(), pos.getLongFlag(), order.getPrice(), number, pos);
						PcTradeMsg trade = new PcTradeMsg();
						trade.setAccountId(pos.getUserId());
						trade.setAsset(pos.getAsset());
						trade.setSymbol(pos.getSymbol());
						trade.setMakerFlag(IntBool.NO);
						trade.setMatchTxId(0L);
						trade.setNumber(number);
						trade.setOpponentOrderId(0L);
						trade.setOrderId(cutOrder.getId());
						trade.setPrice(cutOrder.getPrice());
						trade.setTradeId(0L);
						trade.setTradeTime(now);
						tradeService.handleTradeOrder(trade);
						
						cv = cv.subtract(pos.getVolume());
					}

				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
				startId = order.getId();
			}
			
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
