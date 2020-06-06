package com.hp.sh.expv3.bb.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.base.spring.interceptor.LimitInterceptor;
import com.gitee.hupadev.commons.executor.orderly.OrderlyExecutors;
import com.gitee.hupadev.commons.json.JsonUtils;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.module.fail.entity.BBMqMsg;
import com.hp.sh.expv3.bb.module.fail.service.BBMqMsgService;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.module.order.service.BBOrderQueryService;
import com.hp.sh.expv3.bb.mq.listen.mq.MatchMqHandler;
import com.hp.sh.expv3.bb.mq.msg.in.BBCancelledMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BBTradeMsg;
import com.hp.sh.expv3.bb.mq.msg.out.OrderRebaseMsg;
import com.hp.sh.expv3.bb.mq.send.MatchMqSender;
import com.hp.sh.expv3.utils.DbDateUtils;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/bb/maintain")
public class BBMaintainAction{
	private static final Logger logger = LoggerFactory.getLogger(BBMaintainAction.class);
	
	@Autowired
	private BBOrderQueryService orderQueryService;
	
	@Autowired
	private MatchMqSender matchMqSender;
	
	@Autowired
	private BBOrderApiAction orderApiAction;
	
	@Autowired
	private OrderlyExecutors tradeExecutors;
	
	@Autowired
	private BBMqMsgService mqMsgService;
	
	@Autowired
	private MatchMqHandler mqHandler;
	
	@ApiOperation(value = "querySynchFee")
	@GetMapping(value = "/querySynchFee")
	public List<BBOrderTrade> querySynchFee(){
		Long startTime = DbDateUtils.now()-1000*60*10;
		Page page = new Page(1, 100, 1000L);
		List<BBOrderTrade> list = orderQueryService.querySynchFee(page, "USDT", "BTC_USDT", startTime);
		return list;
	}
	
	@ApiOperation(value = "urlSets")
	@GetMapping(value = "/urlSets")
	public Map urlSets(){
		Map map = new HashMap();
		map.put("isSysClose", LimitInterceptor.isSysClose());
		map.put("urlsets", LimitInterceptor.getUrlsets());
		return map;
	}
	
	@ApiOperation(value = "sleepUrl")
	@GetMapping(value = "/sleepUrl")
	public void sleepUrl(String url, long sleep){
		LimitInterceptor.set(url, false, sleep);
	}
	
	@ApiOperation(value = "unSleepUrl")
	@GetMapping(value = "/unSleepUrl")
	public void unSleepUrl(String url){
		LimitInterceptor.remove(url);
	}

	@ApiOperation(value = "version")
	@GetMapping(value = "/version")
	public Integer version(){
		return 1001;
	}

	@ApiOperation(value = "queryResend")
	@GetMapping(value = "/queryResend")	
	public Integer queryResend(String asset, String symbol){
		long now = DbDateUtils.now()-2000;
		int n = 0;
		Long startId = null;
		Page page = new Page(1, 200, 1000L);
		while(true){
			List<BBOrder> list = orderQueryService.queryPendingActive(page, asset, symbol, now, OrderStatus.PENDING_NEW, startId);
			if(list==null||list.isEmpty()){
				break;
			}
			
			for(BBOrder order : list){
				boolean isExist = matchMqSender.exist(order.getAsset(), order.getSymbol(), ""+order.getId(), order.getCreated());
				if(!isExist){
					n++;
				}
				
				startId = order.getId();
			}
			
		}
		return n;
	}


	@ApiOperation(value = "resendPending")
	@GetMapping(value = "/resendPending")	
	public Map resendPending(String asset, String symbol){
		this.sendRebase(asset, symbol);
		
		Map map = new HashMap();
		
		Integer resendPendingCancel = this.resendPendingCancel(asset, symbol);
		Integer resendPendingNew = this.resendPendingNew(asset, symbol);
		Integer resendNew = this.resendNew(asset, symbol);
		
		map.put("resendPendingCancel", resendPendingCancel);
		map.put("resendPendingNew", resendPendingNew);
		map.put("resendNew", resendNew);
		return map;
	}

	@ApiOperation(value = "sendRebase")
	@GetMapping(value = "/sendRebase")	
	public void sendRebase(String asset, String symbol){
		this.matchMqSender.sendRebaseMsg(new OrderRebaseMsg(asset, symbol));
	}

	@ApiOperation(value = "sendAllRebase")
	@GetMapping(value = "/sendAllRebase")
	public Map sendAllRebase(){
		Map result = new HashMap();
		String asset = "USDT";
		String[] symbols = {"BTC_USDT","EOS_USDT","ETH_USDT","LTC_USDT","BCH_USDT","BSV_USDT","BYS_USDT","ETC_USDT","XRP_USDT"};
		for(String symbol : symbols){
			this.matchMqSender.sendRebaseMsg(new OrderRebaseMsg(asset, symbol));
			Map map = this.resendPending(asset, symbol);
			result.put(asset+"__"+symbol, map);
		}
		
		return result;
	}

	@ApiOperation(value = "cancelAll")
	@GetMapping(value = "/cancelAll")
	public Map cancelAll(){
		Map result = new HashMap();
		String asset = "USDT";
		String[] symbols = {"BTC_USDT","EOS_USDT","ETH_USDT","LTC_USDT","BCH_USDT","BSV_USDT","BYS_USDT","ETC_USDT","XRP_USDT"};
		for(String symbol : symbols){
			List<BBOrder> list1 = this.orderQueryService.queryPendingActive(null, asset, symbol, null, null, null);
			for(BBOrder order: list1){
				this.orderApiAction.setCancelled(order.getUserId(), asset, symbol, order.getId());
			}
		}
		
		return result;
	}

	@ApiOperation(value = "resendPendingCancel")
	@GetMapping(value = "/resendPendingCancel")	
	public Integer resendPendingCancel(String asset, String symbol){
		int n = 0;
		Page page = new Page(1, 200, 1000L);
		long now = DbDateUtils.now()-2000;
		Long startId = null;
		while(true){
			List<BBOrder> list = orderQueryService.queryPendingActive(page, asset, symbol, now, OrderStatus.PENDING_CANCEL, startId);
			if(list==null||list.isEmpty()){
				break;
			}
			
			for(BBOrder order : list){
				boolean isExist = matchMqSender.exist(order.getAsset(), order.getSymbol(), ""+order.getId(), order.getCreated());
				if(!isExist){
					orderApiAction.sendOrderMsg(order);
					n++;
				}
				startId = order.getId();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return n;
	}

	@ApiOperation(value = "resendPendingNew")
	@GetMapping(value = "/resendPendingNew")	
	public Integer resendPendingNew(String asset, String symbol){
		int n = 0;
		Page page = new Page(1, 200, 1000L);
		long now = DbDateUtils.now()-2000;
		Long startId = null;
		while(true){
			List<BBOrder> list = orderQueryService.queryPendingActive(page, asset, symbol, now, OrderStatus.PENDING_NEW, startId);
			if(list==null||list.isEmpty()){
				break;
			}
			
			for(BBOrder order : list){
				boolean isExist = matchMqSender.exist(order.getAsset(), order.getSymbol(), ""+order.getId(), order.getCreated());
				if(!isExist){
					orderApiAction.sendOrderMsg(order);
					n++;
				}
				startId = order.getId();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
			
		}
		return n;
	}

	@ApiOperation(value = "resendNew")
	@GetMapping(value = "/resendNew")	
	public Integer resendNew(String asset, String symbol){
		int n = 0;
		Page page = new Page(1, 200, 1000L);
		long now = DbDateUtils.now()-2000;
		Long startId = null;
		while(true){
			List<BBOrder> list = orderQueryService.queryPendingActive(page, asset, symbol, now, OrderStatus.NEW, startId);
			if(list==null||list.isEmpty()){
				break;
			}
			
			for(BBOrder order : list){
				boolean isExist = matchMqSender.exist(order.getAsset(), order.getSymbol(), ""+order.getId(), order.getCreated());
				if(!isExist){
					orderApiAction.sendOrderMsg(order);
					n++;
				}
				startId = order.getId();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
			
		}
		return n;
	}
	
	@ApiOperation(value = "handleNextFailMsg")
	@GetMapping(value = "/mq/handleNextFailMsg")	
	public Integer handleNextFailMsg(String tag, String symbol){
		BBMqMsg msg = mqMsgService.findFirst(tag, symbol);
		
		return this.handleNextFailMsg(msg);
	}
	
	@ApiOperation(value = "reHandleAllFailMsg")
	@GetMapping(value = "/mq/reHandleAllFailMsg")	
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
			BBTradeMsg mqMsg = JsonUtils.toObject(msg.getBody(), BBTradeMsg.class);
			this.mqHandler.handleTrade(mqMsg);
		}else if(msg.getTag().equals(MqTags.TAGS_CANCELLED)){
			BBCancelledMsg mqMsg = JsonUtils.toObject(msg.getBody(), BBCancelledMsg.class);
			mqHandler.setCancelled(mqMsg.getAccountId(), mqMsg.getAsset(), mqMsg.getSymbol(), mqMsg.getOrderId());
		}
		
		mqMsgService.delete(msg.getUserId(), msg.getId());
		
		return 1;
	}
	
	@ApiOperation(value = "time")
	@GetMapping(value = "/sys/time")	
	public Long sysTime(){
		return System.currentTimeMillis();
	}

	@ApiOperation(value = "thead")
	@GetMapping(value = "/thead/queueSizeMap")	
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
