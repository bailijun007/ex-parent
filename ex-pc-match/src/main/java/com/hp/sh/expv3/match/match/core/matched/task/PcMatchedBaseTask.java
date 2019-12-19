/**
 * @author 10086
 * @date 2019/10/25
 */
package com.hp.sh.expv3.match.match.core.matched.task;

import com.hp.sh.expv3.match.config.setting.PcmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.PcmatchConst;
import com.hp.sh.expv3.match.thread.def.BaseTask;
import com.hp.sh.expv3.match.util.RedisKeyUtil;
import com.hp.sh.expv3.match.util.RedisUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;

public abstract class PcMatchedBaseTask extends BaseTask {

    private String assetSymbol;
    private String asset;
    private String symbol;
    private Long currentMsgOffset;

    private BigDecimal lastPrice;

    @Override
    public boolean onError(Exception ex) {
        LoggerFactory.getLogger(this.getClass()).error(this.getClass().getName() + " error:" + ex.getMessage(), ex);
        return false;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getCurrentMsgOffset() {
        return currentMsgOffset;
    }

    public void setCurrentMsgOffset(Long currentMsgOffset) {
        this.currentMsgOffset = currentMsgOffset;
    }

    @Autowired
    private PcmatchRedisKeySetting pcmatchRedisKeySetting;

    @Autowired
    @Qualifier(PcmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil pcRedisUtil;

    protected void updateSentMqOffset() {
        String key = RedisKeyUtil.buildOrderSentMqOffsetRedisKeyPattern(pcmatchRedisKeySetting.getPcOrderSentMqOffsetRedisKeyPattern(), this.getAsset(), this.getSymbol());
        pcRedisUtil.set(key, "" + this.getCurrentMsgOffset());
    }

}
