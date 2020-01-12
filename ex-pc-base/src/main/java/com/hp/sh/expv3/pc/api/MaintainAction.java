package com.hp.sh.expv3.pc.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.mq.MatchMqSender;

import io.swagger.annotations.ApiOperation;

@RestController
public class MaintainAction{
	@Autowired
	private PcOrderService pcOrderService;
	
	@Autowired
	private MatchMqSender matchMqSender;
	
	@Autowired
	private PcOrderApiAction pcOrderApiAction;
	

	@ApiOperation(value = "rebase")
	@GetMapping(value = "/api/pc/maintain/rebase")	
	public List<PcOrder> rebase(){
		List<PcOrder> list = pcOrderService.queryActiveOrder(null, null, null, null);
		for(PcOrder order : list){
			pcOrderApiAction.sendOrderMsg(order);
		}
		return list;
	}

}
