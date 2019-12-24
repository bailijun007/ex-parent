package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.error.DepositRecordExtErrorCode;
import com.hp.sh.expv3.fund.extension.error.FundAccountExtErrorCode;
import com.hp.sh.expv3.fund.extension.service.FundAccountExtendService;
import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资金账户相关请求
 *
 * @author BaiLiJun  on 2019/12/13
 */
@RestController
public class FundAccountExtApiAction implements FundAccountExtApi {
    @Autowired
    private FundAccountExtendService fundAccountExtendServer;

    @Autowired
    private WithdrawalRecordExtService withdrawalRecordExtServer;

    @Override
    @ApiOperation("获取资金账户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC")
    })
    public CapitalAccountVo getCapitalAccount(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset) {
        if (userId == null) {
            throw new ExException(FundAccountExtErrorCode.PARAM_EMPTY);
        }
        CapitalAccountVo capitalAccount = fundAccountExtendServer.getCapitalAccount(userId, asset);
        if (null == capitalAccount) {
            throw new ExException(DepositRecordExtErrorCode.ACCOUNT_NOT_FIND);
        }

        //查询冻结资金
        BigDecimal frozenCapital = withdrawalRecordExtServer.getFrozenCapital(userId, asset);
        capitalAccount.setLock(frozenCapital);
        capitalAccount.setTotalAssets(frozenCapital.add(capitalAccount.getAvailable()));
        return capitalAccount;
    }

    @Override
    public PageResult<CapitalAccountVo> findFundAccountList(Long userId, String asset, Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            throw new ExException(FundAccountExtErrorCode.PARAM_EMPTY);
        }
        PageResult<CapitalAccountVo> result = new PageResult();
        List<CapitalAccountVo> voList = fundAccountExtendServer.fundAccountList(userId, asset);
        if (!CollectionUtils.isEmpty(voList)) {
            for (CapitalAccountVo vo : voList) {
                //查询冻结资金
                BigDecimal frozenCapital = withdrawalRecordExtServer.getFrozenCapital(vo.getAccountId(), vo.getAsset());
                vo.setLock(frozenCapital);
                vo.setTotalAssets(frozenCapital.add(vo.getAvailable()));
            }

            List<CapitalAccountVo> list = voList.stream().skip(pageSize * (pageNo - 1)).limit(pageSize).collect(Collectors.toList());
            result.setList(list);
        }

        Integer rowTotal = voList.size();
        result.setPageNo(pageNo);
        result.setRowTotal(Long.valueOf(rowTotal + ""));
        result.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return result;
    }


}
