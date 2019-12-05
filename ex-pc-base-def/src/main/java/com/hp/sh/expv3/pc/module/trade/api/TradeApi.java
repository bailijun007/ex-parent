package com.hp.sh.expv3.pc.module.trade.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.hp.sh.expv3.pc.module.trade.api.request.PcTrade;

@FeignClient(value="ex-pc-base")
public interface TradeApi {

	@PostMapping(value = "/api/pc/trade/save")
	void save(List<PcTrade> list) throws Exception;

}