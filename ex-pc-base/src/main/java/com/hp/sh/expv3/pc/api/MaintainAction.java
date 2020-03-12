package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.pc.component.MarkPriceService;
import com.hp.sh.expv3.pc.constant.OrderStatus;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.service.PcOrderQueryService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.service.PcLiqService;
import com.hp.sh.expv3.pc.module.position.service.PcPositionDataService;
import com.hp.sh.expv3.pc.module.position.service.PcPositionMarginService;
import com.hp.sh.expv3.pc.mq.MatchMqSender;
import com.hp.sh.expv3.pc.vo.response.MarkPriceVo;
import com.hp.sh.expv3.utils.DbDateUtils;

import io.swagger.annotations.ApiOperation;

@RestController
public class MaintainAction{
	private static final Logger logger = LoggerFactory.getLogger(MaintainAction.class);
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
    
    @Autowired
    private MarkPriceService markPriceService;
	
	@ApiOperation(value = "version")
	@GetMapping(value = "/api/pc/maintain/version")
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
			List<PcOrder> list = orderQueryService.queryPendingActive(page, now, OrderStatus.PENDING_NEW);
			if(list==null||list.isEmpty()){
				break;
			}
			
			for(PcOrder order : list){
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
			List<PcOrder> list = orderQueryService.queryPendingActive(page, now, OrderStatus.PENDING_CANCEL);
			if(list==null||list.isEmpty()){
				break;
			}
			
			for(PcOrder order : list){
				boolean isExist = matchMqSender.exist(order.getAsset(), order.getSymbol(), ""+order.getId(), order.getCreated());
				if(!isExist){
					pcOrderApiAction.sendOrderMsg(order);
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
			List<PcOrder> list = orderQueryService.queryPendingActive(page, now, OrderStatus.PENDING_NEW);
			if(list==null||list.isEmpty()){
				break;
			}
			
			for(PcOrder order : list){
				boolean isExist = matchMqSender.exist(order.getAsset(), order.getSymbol(), ""+order.getId(), order.getCreated());
				if(!isExist){
					pcOrderApiAction.sendOrderMsg(order);
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
	public boolean checkLiq(Long userId, Long posId){
		PcPosition pos = this.positionDataService.getPosition(userId, posId);
		MarkPriceVo markPriceVo = markPriceService.getLastMarkPrice(pos.getAsset(), pos.getSymbol());
		return liqService.checkLiqStatus(pos, markPriceVo.getMarkPrice());
	}

	@ApiOperation(value = "forceClose")
	@GetMapping(value = "/api/pc/maintain/liq/forceClose")
	public void forceClose(Long userId, String asset, String symbol, Long posId){
		PcPosition pos = this.positionDataService.getPosition(userId, posId);
		liqService.forceClose(pos);
	}
	
}
