/**
 * @author zw
 * @date 2019/9/6
 */
package com.hp.sh.expv3.match.match.core.matched.task;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.bo.PcOrderSnapshotBo;
import com.hp.sh.expv3.match.config.setting.PcmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.PcmatchConst;
import com.hp.sh.expv3.match.util.JsonUtil;
import com.hp.sh.expv3.match.util.RedisKeyUtil;
import com.hp.sh.expv3.match.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Scope("prototype")
public class PcMatchedBookSnapshotTask extends PcMatchedBaseTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<PcOrder4MatchBo> limitAskOrders;
    private List<PcOrder4MatchBo> limitBidOrders;

    private BigDecimal lastPrice;

    public List<PcOrder4MatchBo> getLimitAskOrders() {
        return limitAskOrders;
    }

    public void setLimitAskOrders(List<PcOrder4MatchBo> limitAskOrders) {
        this.limitAskOrders = limitAskOrders;
    }

    public List<PcOrder4MatchBo> getLimitBidOrders() {
        return limitBidOrders;
    }

    public void setLimitBidOrders(List<PcOrder4MatchBo> limitBidOrders) {
        this.limitBidOrders = limitBidOrders;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    @Override
    public void onSucess() {
    }

    @Autowired
    private PcmatchRedisKeySetting pcmatchPcmatchRedisKeySetting;

    @Autowired
    @Qualifier(PcmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil pcRedisUtil;

    @Override
    public void run() {

        PcOrderSnapshotBo snapshot = new PcOrderSnapshotBo();
        snapshot.setLastPrice(lastPrice);
        snapshot.setLimitAskOrders(limitAskOrders);
        snapshot.setLimitBidOrders(limitBidOrders);
        snapshot.setRmqNextOffset(this.getCurrentMsgOffset() + 1);

        String snapshotRedisKey = RedisKeyUtil.buildPcOrderSnapshotRedisKey(pcmatchPcmatchRedisKeySetting.getPcOrderSnapshotRedisKeyPattern(), this.getAsset(), this.getSymbol());

        pcRedisUtil.set(snapshotRedisKey, JsonUtil.toJsonString(snapshot));

        logger.info("save snapshot at {}", System.currentTimeMillis());

    }
}