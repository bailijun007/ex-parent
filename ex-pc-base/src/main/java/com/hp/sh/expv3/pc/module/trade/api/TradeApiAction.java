package com.hp.sh.expv3.pc.module.trade.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.hp.sh.expv3.pc.module.trade.api.request.PcTrade;
import com.hp.sh.expv3.pc.module.trade.service.TradeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags="成交接口")
@RestController
public class TradeApiAction implements TradeApi {
	
	@Autowired
	private TradeService tradeService;

	@Override
	@ApiOperation(value = "保存成交")
	public void save(@RequestBody List<PcTrade> list) throws Exception{
		tradeService.batchSave(BeanHelper.copyList(list, com.hp.sh.expv3.pc.module.trade.entity.PcTrade.class));
	}

}
