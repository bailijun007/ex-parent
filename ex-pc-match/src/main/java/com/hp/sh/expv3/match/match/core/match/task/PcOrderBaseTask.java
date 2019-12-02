/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.hp.sh.expv3.match.thread.def.BaseTask;
import com.hp.sh.expv3.match.thread.def.ITask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PcOrderBaseTask extends BaseTask implements ITask {

    final Logger logger = LoggerFactory.getLogger(getClass());
    private String asset;
    private String symbol;
    private String assetSymbol;
    private Long currentMsgOffset;

    public Logger getLogger() {
        return logger;
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

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
    }

    public Long getCurrentMsgOffset() {
        return currentMsgOffset;
    }

    public void setCurrentMsgOffset(Long currentMsgOffset) {
        this.currentMsgOffset = currentMsgOffset;
    }
}