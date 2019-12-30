package com.hp.sh.expv3.fund.extension.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrParam;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Api(tags ="提币地址扩展Api")
@FeignClient(value="ex-fund-account")
public interface WithdrawalAddrExtApi {

    @ApiOperation("查询提币地址")
    @PostMapping(value = "/api/extension/account/withdraw/address/query")
    public List<WithdrawalAddrVo> findWithdrawalAddr(@RequestBody WithdrawalAddrParam param);


}
