/**
 * 
 */
package com.hp.sh.expv3.fund.wallet.api;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.sh.expv3.fund.wallet.vo.request.FundAddRequest;
import com.hp.sh.expv3.fund.wallet.vo.request.FundCutRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 账户核心服务
 * @author wangjg
 */
@Api(tags = "资金账户接口")
@FeignClient(value = "ex-fund-account")
public interface FundAccountCoreApi {
	
	@ApiOperation(value = "账户是否存在")
	@GetMapping(value = "/api/fund_account/account/exist")
	Boolean accountExist(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);

	@ApiOperation(value = "创建资金账户")
	@GetMapping(value = "/api/fund_account/account/create")
	public int createAccount(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);

	@ApiOperation(value = "加钱")
	@PostMapping(value = "/api/fund_account/money/add")
	public void add(@RequestBody FundAddRequest request);

	@ApiOperation(value = "扣钱")
	@PostMapping(value = "/api/fund_account/money/cut")
	public void cut(@RequestBody FundCutRequest request);

	@ApiOperation(value = "查询资金记录是否存在")
	@GetMapping(value = "/api/fund_account/record/check_exist")
	public Boolean checkTradNo(@RequestParam("userId") Long userId, @RequestParam("tradeNo") String tradeNo);

	@ApiOperation(value = "获取账户余额")
	@GetMapping(value = "/api/fund_account/account/balance")
	BigDecimal getBalance(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);

}