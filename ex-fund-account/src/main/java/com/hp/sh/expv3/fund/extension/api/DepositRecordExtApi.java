package com.hp.sh.expv3.fund.extension.api;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.constant.CapitalAccountErrorCode;
import com.hp.sh.expv3.fund.extension.service.DepositAddrExtService;
import com.hp.sh.expv3.fund.extension.service.DepositRecordExtService;
import com.hp.sh.expv3.fund.extension.service.FundAccountExtendServer;
import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtServer;
import com.hp.sh.expv3.fund.extension.vo.DepositRecordHistoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 充值记录扩展Api
 *
 * @author BaiLiJun  on 2019/12/14
 */
@RestController
@Api(tags = "充值记录扩展Api")
@RequestMapping("/baseUrl/account/deposit")
public class DepositRecordExtApi {

    @Autowired
    private DepositAddrExtService depositAddrExtService;

    @Autowired
    private DepositRecordExtService depositRecordExtService;

    @ApiOperation("查询充币历史记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC"),
            @ApiImplicitParam(name = "queryId", value = "充币记录表主键编号", example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    @GetMapping(value = "/queryHistory")
    public List<DepositRecordHistoryVo> queryHistory(@RequestParam(value = "userId") Long userId, @RequestParam(value = "asset", required = false) String asset,
                                                     @RequestParam(value = "queryId", required = true) Long queryId, @RequestParam(value = "pageSize") Integer pageSize) {

        if (userId == null || pageSize == null || queryId == null) {
            throw new ExException(CapitalAccountErrorCode.PARAM_EMPTY);
        }
        List<DepositRecordHistoryVo> list = depositRecordExtService.queryHistory(userId, asset, queryId, pageSize);
        for (DepositRecordHistoryVo historyVo : list) {
            String addr = depositAddrExtService.getAddressByUserIdAndAsset(historyVo.getUserId(), historyVo.getAsset());
            historyVo.setAddress(addr);
        }

        return list;
    }


}
