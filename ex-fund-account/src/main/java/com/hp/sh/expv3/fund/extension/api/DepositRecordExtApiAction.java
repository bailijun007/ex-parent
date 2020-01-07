package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.error.DepositExtError;
import com.hp.sh.expv3.fund.extension.error.FundAccountExtError;
import com.hp.sh.expv3.fund.extension.error.FundCommonError;
import com.hp.sh.expv3.fund.extension.service.DepositAddrExtService;
import com.hp.sh.expv3.fund.extension.service.DepositRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.AddressVo;
import com.hp.sh.expv3.fund.extension.vo.DepositRecordHistoryVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 充值记录扩展Api
 *
 * @author BaiLiJun  on 2019/12/14
 */
@RestController
@Api(tags = "充值记录扩展Api")
public class DepositRecordExtApiAction implements DepositRecordExtApi {

    @Autowired
    private DepositAddrExtService depositAddrExtService;

    @Autowired
    private DepositRecordExtService depositRecordExtService;

    @Override
    public List<DepositRecordHistoryVo> queryHistory(@RequestParam(value = "userId") Long userId, @RequestParam(value = "asset", required = false) String asset,
                                                     @RequestParam(value = "queryId", required = false) Long queryId, @RequestParam(value = "pageSize") Integer pageSize,
                                                     @RequestParam(value = "pageStatus") Integer pageStatus) {
        if (userId == null || pageSize == null) {
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }
        List<DepositRecordHistoryVo> list = getDepositRecordHistoryVos(userId, asset, queryId, pageSize, pageStatus);

        return list;
    }

    @Override
    public PageResult<DepositRecordHistoryVo> queryAllUserHistory(Long userId, String asset, Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }
        PageResult<DepositRecordHistoryVo> result = depositRecordExtService.pageQueryDepositRecordHistory(userId, asset, pageNo, pageSize);
        if(!CollectionUtils.isEmpty(result.getList())){
            for (DepositRecordHistoryVo historyVo : result.getList()) {
                String addr = depositAddrExtService.getAddressByUserIdAndAsset(historyVo.getUserId(), historyVo.getAsset());
                historyVo.setAddress(addr);
            }
        }


        return result;
    }

    private List<DepositRecordHistoryVo> getDepositRecordHistoryVos(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        List<DepositRecordHistoryVo> list = depositRecordExtService.queryHistory(userId, asset, queryId, pageSize, pageStatus);
        String addr = depositAddrExtService.getAddressByUserIdAndAsset(userId, asset);
        for (DepositRecordHistoryVo historyVo : list) {
            historyVo.setAddress(addr);
        }
        return list;
    }


}
