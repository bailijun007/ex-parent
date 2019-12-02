/**
 * 
 */
package com.hp.sh.expv3.fund.cash.api;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wangjg
 */
@FeignClient(value = "ex-fund-account")
public interface ChainCasehApi {

	@GetMapping(value = "/api/cash/bys/getAddress")
	public String getBysAddress(@RequestParam("userId") Long userId, String asset);

	@GetMapping(value = "/api/cash/bys/createDeposit")
	public String createDeposit(
			@RequestParam("userId") Long userId,
			@RequestParam("chainOrderId") String chainOrderId, 
			@RequestParam("symbolId") String symbolId, 
			@RequestParam("account") String account,
			@RequestParam("amount") BigDecimal amount);

	@GetMapping(value = "/api/cash/bys/createBysDraw")
	public void createDraw(
			@RequestParam("userId") Long userId,
			@RequestParam("asset") String asset, 
			@RequestParam("address") String address, 
			@RequestParam("amount") BigDecimal amount);

	
	@GetMapping(value = "/api/cash/bys/depositSuccess")
	void depositSuccess(@RequestParam("userId") Long userId, @RequestParam("sn") String sn);

	@GetMapping(value = "/api/cash/bys/depositFail")
	void depositFail(@RequestParam("userId") Long userId, @RequestParam("sn") String sn);

}
