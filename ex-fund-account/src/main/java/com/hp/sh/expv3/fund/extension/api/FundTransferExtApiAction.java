package com.hp.sh.expv3.fund.extension.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.error.ExFundError;
import com.hp.sh.expv3.fund.extension.service.FundTransferExtService;
import com.hp.sh.expv3.fund.extension.vo.FundTransferExtVo;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@RestController
public class FundTransferExtApiAction implements FundTransferExtApi {
    @Autowired
    private FundTransferExtService fundTransferExtService;

    @Override
    public PageResult<FundTransferExtVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        if (userId == null || pageSize == null || pageStatus == null) {
            throw new ExException(ExFundError.PARAM_EMPTY);
        }

        return fundTransferExtService.pageQueryHistory(userId, asset, queryId, pageSize, pageStatus);
    }

    @Override
    public PageResult<FundTransferExtVo> queryAllUserHistory(Long userId, String asset,Integer status, Integer pageSize, Integer pageNo) {
        if (pageSize == null || pageNo == null) {
            throw new ExException(ExFundError.PARAM_EMPTY);
        }

        PageResult<FundTransferExtVo> result = fundTransferExtService.queryAllUserHistory(userId, asset,status,pageNo,pageSize);

        return result;
    }
}
