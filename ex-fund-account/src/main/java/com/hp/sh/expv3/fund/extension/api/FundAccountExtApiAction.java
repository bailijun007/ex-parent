package com.hp.sh.expv3.fund.extension.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.constants.C2cConst;
import com.hp.sh.expv3.fund.c2c.service.QueryService;
import com.hp.sh.expv3.fund.constant.ApprovalStatus;
import com.hp.sh.expv3.fund.constant.PaymentStatus;
import com.hp.sh.expv3.fund.extension.error.ExFundError;
import com.hp.sh.expv3.fund.extension.service.FundAccountExtendService;
import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

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

    @Autowired
    private QueryService queryService;

    @Override
    @ApiOperation("获取资金账户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC")
    })
    public CapitalAccountVo getCapitalAccount(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset) {
        if (userId == null) {
            throw new ExException(ExFundError.PARAM_EMPTY);
        }
        CapitalAccountVo capitalAccount = fundAccountExtendServer.getCapitalAccount(userId, asset);
        if (null == capitalAccount) {
             CapitalAccountVo accountVo = new CapitalAccountVo();
            accountVo.setAccountId(userId);
            accountVo.setAsset(asset);
            accountVo.setAvailable(BigDecimal.ZERO);
            accountVo.setLock(BigDecimal.ZERO);
            accountVo.setTotalAssets(BigDecimal.ZERO);
            return accountVo;
//            throw new ExException(ExFundError.ACCOUNT_NOT_FIND);
        }
        //检查c2c 被冻结的资产
        BigDecimal c2cLockedVolume =BigDecimal.ZERO;
        if(asset.equals(C2cConst.LOCKED_ASSET)){
            c2cLockedVolume = queryService.getLockC2cNumber(userId,asset);
        }

        //查询冻结资金
        BigDecimal frozenCapital = withdrawalRecordExtServer.getFrozenCapital(userId, asset);
        //冻结资金 =c2c 被冻结的资产 + 合约冻结资金
        BigDecimal frozen = frozenCapital.add(c2cLockedVolume);
        capitalAccount.setLock(frozen);
        //可用资产
        BigDecimal balance = capitalAccount.getAvailable();
        if(balance.compareTo(BigDecimal.ZERO)==-1){
            throw new ExException(ExFundError.ORDER_NOT_SUFFICIENT_FUNDS);
        }
        capitalAccount.setAvailable(balance);
        capitalAccount.setTotalAssets(frozen.add(balance));
        return capitalAccount;
    }

    @Override
    public PageResult<CapitalAccountVo> findFundAccountList(Long userId, String asset, Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            throw new ExException(ExFundError.PARAM_EMPTY);
        }

        PageResult<CapitalAccountVo> result = fundAccountExtendServer.pageQueryAccountList(userId, asset, pageNo, pageSize);
        if (!CollectionUtils.isEmpty(result.getList())) {
            for (CapitalAccountVo vo : result.getList()) {
                //查询冻结资金
                BigDecimal frozenCapital = withdrawalRecordExtServer.getFrozenCapital(vo.getAccountId(), vo.getAsset());
                vo.setLock(frozenCapital);
                vo.setTotalAssets(frozenCapital.add(vo.getAvailable()));
            }
        }

        return result;
    }

    @Override
    public BigDecimal queryTotalNumber(String asset) {
        if (StringUtils.isEmpty(asset)) {
            throw new ExException(ExFundError.PARAM_EMPTY);
        }
        return fundAccountExtendServer.queryTotalNumber(asset);
    }


}
