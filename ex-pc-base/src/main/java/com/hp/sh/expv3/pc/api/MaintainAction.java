package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.job.LiqHandleResult;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.service.PcOrderQueryService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.service.PcLiqService;
import com.hp.sh.expv3.pc.module.position.service.PcPositionDataService;
import com.hp.sh.expv3.pc.module.position.service.PcPositionMarginService;
import com.hp.sh.expv3.pc.mq.MatchMqSender;

import io.swagger.annotations.ApiOperation;

@RestController
public class MaintainAction{
	@Autowired
	private PcOrderService pcOrderService;
	@Autowired
	private PcOrderQueryService orderQueryService;
	
	@Autowired
	private MatchMqSender matchMqSender;
	
	@Autowired
	private PcOrderApiAction pcOrderApiAction;

	@Autowired
	private PcPositionDataService positionDataService;
	
	@Autowired
	private PcPositionMarginService positionMarginService;
	
	@Autowired
	private PcLiqService liqService;

	@ApiOperation(value = "rebase")
	@GetMapping(value = "/api/pc/maintain/rebase")	
	public List<PcOrder> rebase(){
		List<PcOrder> list = orderQueryService.queryActiveOrder(null, null, null, null);
		for(PcOrder order : list){
			pcOrderApiAction.sendOrderMsg(order);
		}
		return list;
	}

	@ApiOperation(value = "liqmargin")
	@GetMapping(value = "/api/pc/maintain/liq/liqmargin")
	public BigDecimal liqMargin(Long userId, Long posId){
		PcPosition pos = this.positionDataService.getPosition(userId, posId);
		return this.positionMarginService.getLiqMarginDiff(pos);
	}

	@ApiOperation(value = "cutMargin")
	@GetMapping(value = "/api/pc/maintain/liq/cutMargin")
	public void cutMargin(Long userId, String asset, String symbol, Long posId, BigDecimal amount){
		this.positionMarginService.cutMargin(userId, asset, symbol, posId, amount);
	}
	
	@ApiOperation(value = "checkLiq")
	@GetMapping(value = "/api/pc/maintain/liq/checkLiq")
	public LiqHandleResult checkLiq(Long userId, String asset, String symbol, Long posId){
		PcPosition pos = this.positionDataService.getPosition(userId, posId);
		return liqService.checkPosLiq(pos);
	}

	@ApiOperation(value = "forceClose")
	@GetMapping(value = "/api/pc/maintain/liq/forceClose")
	public void forceClose(Long userId, String asset, String symbol, Long posId){
		PcPosition pos = this.positionDataService.getPosition(userId, posId);
		liqService.forceClose(pos);
	}
	
}
