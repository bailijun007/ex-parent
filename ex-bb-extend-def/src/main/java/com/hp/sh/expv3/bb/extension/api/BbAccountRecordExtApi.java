package com.hp.sh.expv3.bb.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author BaiLiJun  on 2020/2/14
 */
@Api(tags = "用户bb账户明细接口")
@FeignClient(value = "ex-bb-extend")
public interface BbAccountRecordExtApi {

    @ApiOperation(value = "查询所有用户币币划转历史记录")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = false),
                    @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
                    @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
                    @ApiImplicitParam(name = "pageSize", value = "页大小", example = "10", required = true)
            }
    )
    @GetMapping(value = "/api/bbAccountRecord/ext/queryHistory")
    PageResult<BbAccountRecordVo> queryHistory(@RequestParam(value = "userId",required = false) Long userId, @RequestParam(value = "asset",required = false) String asset,
                                               @RequestParam(value = "pageSize")  Integer pageSize,  @RequestParam(value = "pageNo") Integer pageNo);


}