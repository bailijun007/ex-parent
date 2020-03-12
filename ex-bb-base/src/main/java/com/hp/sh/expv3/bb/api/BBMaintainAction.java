package com.hp.sh.expv3.bb.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.job.BBMatchedHandler;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.service.BBOrderQueryService;
import com.hp.sh.expv3.bb.mq.send.MatchMqSender;
import com.hp.sh.expv3.component.executor.OrderlyExecutors;
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

	@ApiOperation(value = "version")
	@GetMapping(value = "/api/bb/maintain/version")
	public Integer version(){
		return 1001;
	}

	@ApiOperation(value = "queryResend")
	@GetMapping(value = "/api/pc/maintain/queryResend")	
	public Integer queryResend(){
		long now = DbDateUtils.now()-2000;
		int n = 0;
		Page page = new Page(1, 200, 1000L);
		while(true){
			List<BBOrder> list = orderQueryService.queryPendingActive(page, now, OrderStatus.PENDING_NEW);
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
	@GetMapping(value = "/api/pc/maintain/resendPending")	
	public Map resendPending(){
		Map map = new HashMap();
		Integer resendPendingCancel = this.resendPendingCancel();
		Integer resendPendingNew = this.resendPendingNew();
		map.put("resendPendingCancel", resendPendingCancel);
		map.put("resendPendingNew", resendPendingNew);
		return map;
	}

	@ApiOperation(value = "resendPendingCancel")
	@GetMapping(value = "/api/pc/maintain/resendPendingCancel")	
	public Integer resendPendingCancel(){
		int n = 0;
		Page page = new Page(1, 200, 1000L);
		long now = DbDateUtils.now()-2000;
		while(true){
			List<BBOrder> list = orderQueryService.queryPendingActive(page, now, OrderStatus.PENDING_CANCEL);
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
	@GetMapping(value = "/api/pc/maintain/resendPendingNew")	
	public Integer resendPendingNew(){
		int n = 0;
		Page page = new Page(1, 200, 1000L);
		long now = DbDateUtils.now()-2000;
		while(true){
			List<BBOrder> list = orderQueryService.queryPendingActive(page, now, OrderStatus.PENDING_NEW);
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
		Map<Integer,Integer> map = tradeExecutors.getQueueSizeMap();
		int total = 0;
		for(int n : map.values()){
			total += n;
		}
		result.put("total", total);
		result.put("map", tradeExecutors.getQueueSizeMap());
		return result;
	}
}
