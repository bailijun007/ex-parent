package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.job.liq.LiquidationHandler;
import com.hp.sh.expv3.pc.module.liq.service.PcLiqService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.service.PcPositionDataService;
import com.hp.sh.expv3.pc.module.position.service.PcPositionMarginService;
import com.hp.sh.expv3.pc.module.position.service.PcTradeService;
import com.hp.sh.expv3.pc.mq.MatchMqSender;
import com.hp.sh.expv3.pc.strategy.PcStrategyContext;
import com.hp.sh.expv3.utils.math.BigUtils;

import io.swagger.annotations.ApiOperation;

@RestController
public class TestAction{
	@Autowired
	private PcOrderService pcOrderService;
	
	@Autowired
	private PcTradeService pcTradeService;
	
	@Autowired
	private PcLiqService liqService;
	
	@Autowired
	private PcPositionDataService positionDataService;
	
	@Autowired
	private PcStrategyContext strategyContext;
	
	@Autowired
	private FeeRatioService feeRatioService;

	@Autowired
	private MetadataService metadataService;
	
	@Autowired
	private LiquidationHandler liquidationJob;
	
	@Autowired
	private PcPositionMarginService positionMarginService;
	
	@Autowired
	private MatchMqSender matchMqSender;

	@ApiOperation(value = "exist")
	@GetMapping(value = "/api/pc/test/mq/exist")
	public Boolean mqExist(String asset, String symbol, String key, Long createdTime) throws Exception{
		return this.matchMqSender.exist(asset, symbol, key, createdTime);
	}

	@ApiOperation(value = "liqprice")
	@GetMapping(value = "/api/pc/test/calc/liqprice")	
	public BigDecimal liqprice(){
		PcPosition pos = positionDataService.getPosition(117225946850832512L, 136117687829954560L);
		BigDecimal liqprice = strategyContext.calcLiqPrice(pos);
		
		return liqprice;
	}
	
	@ApiOperation(value = "liqjob")
	@GetMapping(value = "/api/pc/test/liq/liqjob")	
	public BigDecimal liqjob(){
		liquidationJob.checkLiqOrder();
		return null;
	}
	
	@ApiOperation(value = "fee")
	@GetMapping(value = "/api/pc/test/fee")	
	public BigDecimal fee(){
		BigDecimal fee1 = feeRatioService.getTakerFeeRatio(109027203634737280L, "BTC", "BTC_USDT");
		BigDecimal fee2 = feeRatioService.getMakerFeeRatio(109027203634737280L, "BTC", "BTC_USDT");
		BigDecimal hr = feeRatioService.getHoldRatio(109027203634737280L, "BTC", "BTC_USDT", BigDecimal.ZERO);
		
		BigDecimal maxlev = metadataService.getMaxLeverage(109027203634737280L, "BTC", "BTC_USDT", BigDecimal.ZERO);
		
		return fee2;
	}

	@ApiOperation(value = "level")
	@GetMapping(value = "/api/pc/test/pos/level")
	public BigDecimal poslevel(){
		List<PcPosition> list = positionDataService.queryActivePosList(null, null, null, null);
		for(PcPosition pos : list){
			BigDecimal hr = this.feeRatioService.getHoldRatio(pos.getUserId(), pos.getAsset(), pos.getSymbol(), pos.getVolume());
			if(BigUtils.ne(hr, pos.getHoldMarginRatio())){
				pos.setHoldMarginRatio(hr);
				this.positionDataService.update(pos);
			}
		}
		return null;
	}

}
