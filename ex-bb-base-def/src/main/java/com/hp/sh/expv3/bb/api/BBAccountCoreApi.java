/**
 * 
 */
package com.hp.sh.expv3.bb.api;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.sh.expv3.bb.vo.request.BBAddRequest;
import com.hp.sh.expv3.bb.vo.request.BBCutRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author wangjg
 */
@Api(tags = "比比合约账户接口")
@FeignClient(value="ex-bb-base")
public interface BBAccountCoreApi {

	@ApiOperation(value = "账户是否存在")
	@GetMapping(value = "/api/bb/account/account/exist")	
	boolean accountExist(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);

	@ApiOperation(value = "创建币币账户")
	@GetMapping(value = "/api/bb/account/account/create")
	public int createAccount(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);

	@ApiOperation(value = "加钱")
	@PostMapping(value = "/api/bb/account/balance/add")
	public Integer add(@RequestBody BBAddRequest request);

	@ApiOperation(value = "扣钱")
	@PostMapping(value = "/api/bb/account/balance/cut")
	public Integer cut(@RequestBody BBCutRequest request);

	@ApiOperation(value = "获取账户余额")
	@GetMapping(value = "/api/bb/account/balance/query")
	BigDecimal getBalance(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);

	@ApiOperation(value = "查询资金记录是否存在")
	@GetMapping(value = "/api/bb/account/record/exist")
	public Boolean checkTradNo(@RequestParam("userId") Long userId, @RequestParam("tradeNo") String tradeNo);

}