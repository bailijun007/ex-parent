package com.hp.sh.expv3.bb.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.BbAccountLogExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountLogExtVo;
import com.hp.sh.expv3.commons.exception.ExException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiLiJun  on 2020/3/24
 */
@RestController
public class BbAccountLogExtAction implements BbAccountLogExtApi {

    @Autowired
    private BbAccountLogExtService bbAccountLogExtService;

    @Override
    public PageResult<BbAccountLogExtVo> query(Long userId, String asset, String symbol, Integer tradeType, Long startDate, Long endDate, Integer pageNo, Integer pageSize) {
        PageResult<BbAccountLogExtVo> result = new PageResult<BbAccountLogExtVo>();
       this.checkParam(userId,asset,symbol,tradeType,startDate,endDate,pageNo,pageSize);

        return null;
    }

    private void checkParam(Long userId, String asset, String symbol, Integer tradeType, Long startDate,
                            Long endDate, Integer pageNo, Integer pageSize) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || tradeType == null || null == userId||
                startDate == null || endDate == null || pageNo == null || pageSize == null){
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }

    }
}
