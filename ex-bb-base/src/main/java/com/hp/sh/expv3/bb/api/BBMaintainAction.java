package com.hp.sh.expv3.bb.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.base.spring.interceptor.LimitInterceptor;
import com.gitee.hupadev.commons.executor.orderly.OrderlyExecutors;
import com.gitee.hupadev.commons.json.JsonUtils;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.job.BBMatchedHandler;
import com.hp.sh.expv3.bb.module.fail.entity.BBMqMsg;
import com.hp.sh.expv3.bb.module.fail.service.BBMqMsgService;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.module.order.service.BBOrderQueryService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.mq.listen.mq.MatchMqConsumer;
import com.hp.sh.expv3.bb.mq.msg.in.BbOrderCancelMqMsg;
import com.hp.sh.expv3.bb.mq.send.MatchMqSender;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.utils.DbDateUtils;

import io.swagger.annotations.ApiOperation;

@RestController
public class BBMaintainAction{
	private static final Logger logger = LoggerFactory.getLogger(BBMaintainAction.class);
	
	@Autowired
	private BBOrderQueryService orderQueryService;
	
	@Autowired
	private MatchMqSender matchMqSender;
	
	@Autowired
	private BBOrderApiAction orderApiAction;
	
	@Autowired
	private BBMatchedHandler matchedHandler;
	
	@Autowired
	private OrderlyExecutors tradeExecutors;
	
	@Autowired
	private BBMqMsgService mqMsgService;
	
	@Autowired
	private MatchMqConsumer matchMqConsumer;
	
	@Autowired
	private BBTradeService tradeService;
	@Autowired
	private BBOrderService orderService;
	
	@ApiOperation(value = "querySynchFee")
	@GetMapping(value = "/api/bb/maintain/querySynchFee")
	public List<BBOrderTrade> querySynchFee(){
		Long startTime = DbDateUtils.now()-1000*60*10;
		Page page = new Page(1, 100, 1000L);
		List<BBOrderTrade> list = orderQueryService.querySynchFee(page, startTime);
		return list;
	}
	
	@ApiOperation(value = "urlSets")
	@GetMapping(value = "/api/bb/maintain/urlSets")
	public Map urlSets(){
		Map map = new HashMap();
		map.put("isSysClose", LimitInterceptor.isSysClose());
		map.put("urlsets", LimitInterceptor.getUrlsets());
		return map;
	}
	
	@ApiOperation(value = "sleepUrl")
	@GetMapping(value = "/api/bb/maintain/sleepUrl")
	public void sleepUrl(String url, long sleep){
		LimitInterceptor.set(url, false, sleep);
	}
	
	@ApiOperation(value = "unSleepUrl")
	@GetMapping(value = "/api/bb/maintain/unSleepUrl")
	public void unSleepUrl(String url){
		LimitInterceptor.remove(url);
	}

	@ApiOperation(value = "version")
	@GetMapping(value = "/api/bb/maintain/version")
	public Integer version(){
		return 1001;
	}

	@ApiOperation(value = "queryResend")
	@GetMapping(value = "/api/bb/maintain/queryResend")	
	public Integer queryResend(String symbol){
		long now = DbDateUtils.now()-2000;
		int n = 0;
		Page page = new Page(1, 200, 1000L);
		while(true){
			List<BBOrder> list = orderQueryService.queryPendingActive(page, symbol, now, OrderStatus.PENDING_NEW);
			if(list==null||list.isEmpty()){
				break;
			}
			
			for(BBOrder order : list){
				boolean isExist = matchMqSender.exist(order.getAsset(), order.getSymbol(), ""+order.getId(), order.getCreated());
				if(!isExist){
					n++;
				}
			}
			
			page.setPageNo(page.getPageNo()+1);
		}
		return n;
	}


	@ApiOperation(value = "resendPending")
	@GetMapping(value = "/api/bb/maintain/resendPending")	
	public Map resendPending(String symbol){
		Map map = new HashMap();
		Integer resendPendingCancel = this.resendPendingCancel(symbol);
		Integer resendPendingNew = this.resendPendingNew(symbol);
		map.put("resendPendingCancel", resendPendingCancel);
		map.put("resendPendingNew", resendPendingNew);
		return map;
	}

	@ApiOperation(value = "resendPendingCancel")
	@GetMapping(value = "/api/bb/maintain/resendPendingCancel")	
	public Integer resendPendingCancel(String symbol){
		int n = 0;
		Page page = new Page(1, 200, 1000L);
		long now = DbDateUtils.now()-2000;
		while(true){
			List<BBOrder> list = orderQueryService.queryPendingActive(page, symbol, now, OrderStatus.PENDING_CANCEL);
			if(list==null||list.isEmpty()){
				break;
			}
			
			for(BBOrder order : list){
				boolean isExist = matchMqSender.exist(order.getAsset(), order.getSymbol(), ""+order.getId(), order.getCreated());
				if(!isExist){
					orderApiAction.sendOrderMsg(order);
					n++;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
			
			page.setPageNo(page.getPageNo()+1);
		}
		return n;
	}

	@ApiOperation(value = "resendPendingNew")
	@GetMapping(value = "/api/bb/maintain/resendPendingNew")	
	public Integer resendPendingNew(String symbol){
		int n = 0;
		Page page = new Page(1, 200, 1000L);
		long now = DbDateUtils.now()-2000;
		while(true){
			List<BBOrder> list = orderQueryService.queryPendingActive(page, symbol, now, OrderStatus.PENDING_NEW);
			if(list==null||list.isEmpty()){
				break;
			}
			
			for(BBOrder order : list){
				boolean isExist = matchMqSender.exist(order.getAsset(), order.getSymbol(), ""+order.getId(), order.getCreated());
				if(!isExist){
					orderApiAction.sendOrderMsg(order);
					n++;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
			
			page.setPageNo(page.getPageNo()+1);
		}
		return n;
	}
	
	@ApiOperation(value = "handleNextFailMsg")
	@GetMapping(value = "/api/bb/maintain/mq/handleNextFailMsg")	
	public Integer handleNextFailMsg(String tag, String symbol){
		BBMqMsg msg = mqMsgService.findFirst(tag, symbol);
		
		return this.handleNextFailMsg(msg);
	}
	
	@ApiOperation(value = "reHandleAllFailMsg")
	@GetMapping(value = "/api/bb/maintain/mq/reHandleAllFailMsg")	
	public String reHandleAllFailMsg(Integer num){
		num = num==null ? 1:num;
		
		List<BBMqMsg> list = this.mqMsgService.findFirstList(num);
		for(BBMqMsg msg : list){
			this.handleNextFailMsg(msg);
		}
		
		return "Over."+list.size();
	}
	
	private int handleNextFailMsg(BBMqMsg msg){
		if(msg==null){
			return 0;
		}
		
		if(msg.getTag().equals(MqTags.TAGS_TRADE)){
			BBTradeVo mqMsg = JsonUtils.toObject(msg.getBody(), BBTradeVo.class);
			this.tradeService.handleTrade(mqMsg);
		}else if(msg.getTag().equals(MqTags.TAGS_CANCELLED)){
			BbOrderCancelMqMsg mqMsg = JsonUtils.toObject(msg.getBody(), BbOrderCancelMqMsg.class);
			orderService.setCancelled(mqMsg.getAccountId(), mqMsg.getAsset(), mqMsg.getSymbol(), mqMsg.getOrderId());
		}
		
		mqMsgService.delete(msg.getUserId(), msg.getId());
		
		return 1;
	}
	
	@ApiOperation(value = "time")
	@GetMapping(value = "/api/bb/maintain/sys/time")	
	public Long sysTime(){
		return System.currentTimeMillis();
	}

	@ApiOperation(value = "job")
	@GetMapping(value = "/api/bb/maintain/job/handle")	
	public void jobHandle(){
		matchedHandler.handlePending();
	}

	@ApiOperation(value = "thead")
	@GetMapping(value = "/api/bb/maintain/thead/queueSizeMap")	
	public Map getQueueSizeMap(){
		Map result = new HashMap();
		Map<Object,Integer> map = tradeExecutors.getQueueSizeMap();
		int total = 0;
		for(int n : map.values()){
			total += n;
		}
		result.put("total", total);
		result.put("map", tradeExecutors.getQueueSizeMap());
		return result;
	}
}
