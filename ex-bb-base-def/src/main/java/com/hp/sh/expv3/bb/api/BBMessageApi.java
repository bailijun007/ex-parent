package com.hp.sh.expv3.bb.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.hp.sh.expv3.bb.vo.result.StatItem;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(tags="消息接口")
@FeignClient(value="ex-bb-base")
public interface BBMessageApi {

	@ApiOperation(value = "按交易对统计")
	@GetMapping(value = "/api/bb/message/statBySymbol")
	List<StatItem> getSymbolStat();

	@ApiOperation(value = "获取总数")
	@GetMapping(value = "/api/bb/message/total")
	BigDecimal getTotal();
	
}