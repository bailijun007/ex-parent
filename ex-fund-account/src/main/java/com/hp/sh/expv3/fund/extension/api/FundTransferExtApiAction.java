package com.hp.sh.expv3.fund.extension.api;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.constant.FundTransferExtErrorCode;
import com.hp.sh.expv3.fund.extension.constant.WithdrawalAddressExtErrorCode;
import com.hp.sh.expv3.fund.extension.service.FundTransferExtService;
import com.hp.sh.expv3.fund.extension.vo.FundTransferExtVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@RestController
public class FundTransferExtApiAction implements FundTransferExtApi {
    @Autowired
    private FundTransferExtService fundTransferExtService;

    @Override
    public List<FundTransferExtVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        if (userId == null ||  pageSize == null || pageStatus == null || StringUtils.isEmpty(asset)) {
            throw new ExException(FundTransferExtErrorCode.PARAM_EMPTY);
        }

        return fundTransferExtService.queryHistory(userId,asset,queryId,pageSize,pageStatus);
    }
}
