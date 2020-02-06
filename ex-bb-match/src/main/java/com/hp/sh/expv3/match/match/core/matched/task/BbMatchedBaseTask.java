/**
 * @author 10086
 * @date 2019/10/25
 */
package com.hp.sh.expv3.match.match.core.matched.task;

import com.hp.sh.expv3.match.config.setting.BbmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.BbmatchConst;
import com.hp.sh.expv3.match.match.core.matched.task.def.BbMatchedTaskService;
import com.hp.sh.expv3.match.thread.def.BaseTask;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import com.hp.sh.expv3.match.util.RedisKeyUtil;
import com.hp.sh.expv3.match.util.RedisUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;

public abstract class BbMatchedBaseTask extends BaseTask {

    private String assetSymbol;
    private String asset;
    private String symbol;
    private Long currentMsgOffset;
    private BigDecimal lastPrice;
    private String currentMsgId;

    public String getCurrentMsgId() {
        return currentMsgId;
    }

    public void setCurrentMsgId(String currentMsgId) {
        this.currentMsgId = currentMsgId;
    }

    private IThreadWorker matchedThreadWorker;

    public IThreadWorker getMatchedThreadWorker() {
        return matchedThreadWorker;
    }

    public void setMatchedThreadWorker(IThreadWorker matchedThreadWorker) {
        this.matchedThreadWorker = matchedThreadWorker;
    }

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
    private BbmatchRedisKeySetting bbmatchRedisKeySetting;
    @Autowired
    private BbMatchedTaskService bbMatchedTaskService;

    @Autowired
    @Qualifier(BbmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil bbRedisUtil;

    protected void updateSentMqOffset() {
        String key = RedisKeyUtil.buildOrderSentMqOffsetRedisKeyPattern(bbmatchRedisKeySetting.getBbOrderSentMqOffsetRedisKeyPattern(), this.getAsset(), this.getSymbol());
        bbRedisUtil.set(key, "" + this.getCurrentMsgOffset());
        bbMatchedTaskService.logQueueSize(asset, symbol, matchedThreadWorker, this);
    }

}