/**
 * 
 */
package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.sh.expv3.pc.api.request.AddMoneyRequest;
import com.hp.sh.expv3.pc.api.request.CutMoneyRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author wangjg
 */
@Api(tags = "合约账户核心接口")
@FeignClient(value="ex-pc-base")
public interface PcAccountCoreApi {

	@ApiOperation(value = "创建资金账户")
	@GetMapping(value = "/api/pc/pc_account/account/create")
	public int createAccount(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);

	@ApiOperation(value = "加钱")
	@PostMapping(value = "/api/pc/pc_account/balance/add")
	public Integer add(@RequestBody AddMoneyRequest request);

	@ApiOperation(value = "扣钱")
	@PostMapping(value = "/api/pc/pc_account/balance/cut")
	public Integer cut(@RequestBody CutMoneyRequest request);

	@ApiOperation(value = "获取账户余额")
	@GetMapping(value = "/api/pc/pc_account/balance/query")
	BigDecimal getBalance(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);

	@ApiOperation(value = "查询资金记录是否存在")
	@GetMapping(value = "/api/pc/pc_account/record/check_exist")
	public Boolean checkTradNo(@RequestParam("userId") Long userId, @RequestParam("tradeNo") String tradeNo);

}