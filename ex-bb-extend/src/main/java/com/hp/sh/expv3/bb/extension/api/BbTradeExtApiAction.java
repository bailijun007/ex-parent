package com.hp.sh.expv3.bb.extension.api;

import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.BbTradeExtService;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import com.hp.sh.expv3.commons.exception.ExException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/15
 */
@RestController
public class BbTradeExtApiAction implements BbTradeExtApi {
   @Autowired
    private BbTradeExtService bbTradeExtService;

    @Override
    public List<BbTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime) {
        if (null == startTime || endTime == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        return bbTradeExtService.selectTradeListByTimeInterval(asset,symbol,startTime,endTime);
    }
}
