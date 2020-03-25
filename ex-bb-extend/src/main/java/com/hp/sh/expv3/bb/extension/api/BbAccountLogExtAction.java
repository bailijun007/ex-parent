package com.hp.sh.expv3.bb.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.BbAccountLogExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountLogExtVo;
import com.hp.sh.expv3.commons.exception.ExException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/24
 */
@RestController
public class BbAccountLogExtAction implements BbAccountLogExtApi {

    @Autowired
    private BbAccountLogExtService bbAccountLogExtService;

    @Override
    public List<BbAccountLogExtVo> query(Long userId, String asset, String symbol, Integer historyType, Integer tradeType, Long startDate, Long endDate, Integer nextPage, Integer lastId, Integer pageSize) {
        List<BbAccountLogExtVo> list = new ArrayList<>();
        this.checkParam(userId, asset, symbol, historyType, tradeType, startDate, endDate, nextPage, pageSize);
        List<BbAccountLogExtVo> voList = null;
        if (null == lastId) {
            voList = bbAccountLogExtService.listBbAccountLogs(userId, asset, symbol, tradeType, startDate, endDate, nextPage, lastId, pageSize);
        } else {
            voList = bbAccountLogExtService.listBbAccountLogs(userId, asset, symbol, tradeType, startDate, endDate, nextPage, lastId, pageSize);
        }
        return list;
    }

    private void checkParam(Long userId, String asset, String symbol, Integer historyType, Integer tradeType, Long startDate,
                            Long endDate, Integer pageNo, Integer pageSize) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || tradeType == null || null == userId ||
                pageNo == null || pageSize == null) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        if (2 == historyType) {
            if (startDate == null || endDate == null) {
                throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
            }
        }


    }
}
