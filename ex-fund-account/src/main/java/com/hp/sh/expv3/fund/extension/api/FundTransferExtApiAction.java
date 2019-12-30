package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.error.FundCommonError;
import com.hp.sh.expv3.fund.extension.error.FundTransferExtError;
import com.hp.sh.expv3.fund.extension.error.AddressExtError;
import com.hp.sh.expv3.fund.extension.service.FundTransferExtService;
import com.hp.sh.expv3.fund.extension.vo.FundTransferExtVo;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@RestController
public class FundTransferExtApiAction implements FundTransferExtApi {
    @Autowired
    private FundTransferExtService fundTransferExtService;

    @Override
    public List<FundTransferExtVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        if (userId == null || pageSize == null || pageStatus == null) {
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }

        return fundTransferExtService.queryHistory(userId, asset, queryId, pageSize, pageStatus);
    }

    @Override
    public PageResult<FundTransferExtVo> queryAllUserHistory(Long userId, String asset, Integer pageSize, Integer pageNo) {
        if (pageSize == null || pageNo == null) {
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }
        PageResult<FundTransferExtVo> result = new PageResult();
        List<FundTransferExtVo> list = fundTransferExtService.queryAllUserHistory(userId, asset);

        if(!CollectionUtils.isEmpty(list)){
            List<FundTransferExtVo> voList = list.stream().skip(pageSize * (pageNo - 1)).limit(pageSize).collect(Collectors.toList());
            result.setList(voList);
        }

        Integer rowTotal = list.size();
        result.setPageNo(pageNo);
        result.setRowTotal(Long.valueOf(rowTotal + ""));
        result.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return result;
    }
}
