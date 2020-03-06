package com.hp.sh.expv3.bb.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.job.BBMatchedHandler;
import com.hp.sh.expv3.bb.job.BBMatchedJob;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.service.BBOrderQueryService;
import com.hp.sh.expv3.bb.mq.send.MatchMqSender;
import com.hp.sh.expv3.component.executor.GroupTask;
import com.hp.sh.expv3.component.executor.OrderlyExecutors;
import com.hp.sh.expv3.utils.DbDateUtils;

import io.swagger.annotations.ApiOperation;

@RestController
public class BBMaintainAction{
	@Autowired
	private BBOrderQueryService orderQueryService;
	
	@Autowired
	private MatchMqSender matchMqSender;
	
	@Autowired
	private BBOrderApiAction bBOrderApiAction;
	
	@Autowired
	private BBMatchedHandler matchedHandler;
	
	@Autowired
	private OrderlyExecutors tradeExecutors;

	@ApiOperation(value = "version")
	@GetMapping(value = "/api/bb/maintain/version")
	public Integer version(){
		return 1001;
	}

	@ApiOperation(value = "rebase")
	@GetMapping(value = "/api/bb/maintain/rebase")	
	public List<BBOrder> rebase(){
		long now = DbDateUtils.now();
		List<BBOrder> list = orderQueryService.queryRebaseOrder(null, now);
		for(BBOrder order : list){
			bBOrderApiAction.sendOrderMsg(order);
		}
		return list;
	}
	

	@ApiOperation(value = "queryResend")
	@GetMapping(value = "/api/bb/maintain/queryResend")	
	public Integer queryResend(){
		long now = DbDateUtils.now();
		int n = 0;
		Page page = new Page(1, 200, 1000L);
		while(true){
			List<BBOrder> list = orderQueryService.queryRebaseOrder(page, now);
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

	@ApiOperation(value = "resend")
	@GetMapping(value = "/api/bb/maintain/resend")	
	public Integer resend(){
		int n = 0;
		Page page = new Page(1, 200, 1000L);
		long now = DbDateUtils.now()-2000;
		while(true){
			List<BBOrder> list = orderQueryService.queryRebaseOrder(page, now);
			if(list==null||list.isEmpty()){
				break;
			}
			
			for(BBOrder order : list){
				boolean isExist = matchMqSender.exist(order.getAsset(), order.getSymbol(), ""+order.getId(), order.getCreated());
				if(!isExist){
					bBOrderApiAction.sendOrderMsg(order);
				}
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			n += list.size();
			
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
