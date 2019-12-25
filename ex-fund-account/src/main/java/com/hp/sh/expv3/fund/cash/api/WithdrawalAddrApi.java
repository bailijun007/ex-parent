package com.hp.sh.expv3.fund.cash.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags ="提现地址接口")
@FeignClient(value = "ex-fund-account")
public interface WithdrawalAddrApi {

	@ApiOperation("保存提现地址")
	@GetMapping(value = "/api/withdrawal_addr/save")
	long save(@RequestParam("userId") long userId, @RequestParam("asset") String asset, @RequestParam("address") String address, @RequestParam("remark") String remark);

	@ApiOperation("更新提现地址备注")
	@GetMapping(value = "/api/withdrawal_addr/updateRemark")
	boolean updateRemark(@RequestParam("userId") long userId, @RequestParam("asset") String asset, @RequestParam("withdrawAddrId") long withdrawAddrId, @RequestParam("remark") String remark);

	@ApiOperation("删除提现地址")
	@GetMapping(value = "/api/withdrawal_addr/delete")
	boolean delete(@RequestParam("userId") long userId, @RequestParam("asset") String asset, @RequestParam("withdrawAddrId") long withdrawAddrId);

}