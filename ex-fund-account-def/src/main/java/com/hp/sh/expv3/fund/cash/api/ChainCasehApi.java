/**
 * 
 */
package com.hp.sh.expv3.fund.cash.api;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.sh.expv3.fund.cash.api.vo.BysCreateResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author wangjg
 */
@Api(tags ="链上充值")
@FeignClient(value = "ex-fund-account")
public interface ChainCasehApi {

	@GetMapping(value = "/api/cash/bys/getDepositAddress")
	public String getDepositAddress(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);

	@ApiOperation(value = "2、验证地址")
	@GetMapping(value = "/api/cash/bys/verifyAddress")
	boolean verifyAddress(@RequestParam("asset") String asset, @RequestParam("address") String address);

	@ApiOperation("创建充币记录")
	@GetMapping(value = "/api/cash/bys/createDeposit")
	public String createDeposit(
			@RequestParam("userId") Long userId,
			@RequestParam("chainOrderId") String chainOrderId, 
			@RequestParam("symbolId") String symbolId, 
			@RequestParam("account") String account,
			@RequestParam("amount") BigDecimal amount, 
			@RequestParam("txHash") String txHash);

	@ApiOperation("创建提现记录")
	@GetMapping(value = "/api/cash/bys/createBysDraw")
	public void createWithdrawal(
			@RequestParam("userId") Long userId,
			@RequestParam("asset") String asset, 
			@RequestParam("address") String address, 
			@RequestParam("amount") BigDecimal amount);

	@ApiOperation("充值成功回掉")
	@GetMapping(value = "/api/cash/bys/depositSuccess")
	void depositSuccess(@RequestParam("userId") Long userId, @RequestParam("sn") String sn);

	@ApiOperation("充值失败回掉")
	@GetMapping(value = "/api/cash/bys/depositFail")
	void depositFail(@RequestParam("userId") Long userId, @RequestParam("sn") String sn);

	@ApiOperation(value = "2、批准提现")
	@GetMapping(value = "/api/cash/bys/draw/approve")
	void approve(@RequestParam("userId") Long userId, @RequestParam("id") Long id);
	
	@ApiOperation(value = "3、拒绝提现")
	@GetMapping(value = "/api/cash/bys/draw/reject")
	void reject(@RequestParam("userId") Long userId, @RequestParam("id") Long id, @RequestParam("remark") String remark);

}
