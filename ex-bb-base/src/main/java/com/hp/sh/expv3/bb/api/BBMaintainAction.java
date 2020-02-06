package com.hp.sh.expv3.bb.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.component.MarkPriceService;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.service.BBOrderQueryService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.mq.MatchMqSender;
import com.hp.sh.expv3.utils.DbDateUtils;

import io.swagger.annotations.ApiOperation;

@RestController
public class BBMaintainAction{
	@Autowired
	private BBOrderService bBOrderService;
	@Autowired
	private BBOrderQueryService orderQueryService;
	
	@Autowired
	private MatchMqSender matchMqSender;
	
	@Autowired
	private BBOrderApiAction bBOrderApiAction;

    @Autowired
    private MarkPriceService markPriceService;
	
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
	
}
