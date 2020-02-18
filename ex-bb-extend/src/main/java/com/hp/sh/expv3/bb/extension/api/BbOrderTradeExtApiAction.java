package com.hp.sh.expv3.bb.extension.api;

import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.BbOrderTradeExtService;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.dev.CrossDB;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiLiJun  on 2020/2/15
 */
@RestController
public class BbOrderTradeExtApiAction implements BbOrderTradeExtApi {
   @Autowired
    private BbOrderTradeExtService bbOrderTradeExtService;

    /**
     * 查小于某个时间点的最大的一条记录
     *
     * @param asset    资产
     * @param symbol   合约交易品种
     * @param statTime 成交时间
     * @return
     */
    @CrossDB
    @Override
    public BbOrderTradeVo selectLessTimeTrade(String asset, String symbol, Long statTime) {
        if (StringUtils.isEmpty(asset)||StringUtils.isEmpty(symbol)||statTime == null) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        return bbOrderTradeExtService.selectLessTimeTrade(asset,symbol,statTime);
    }
}
