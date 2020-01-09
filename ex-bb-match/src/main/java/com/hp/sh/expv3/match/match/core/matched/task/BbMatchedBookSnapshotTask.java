/**
 * @author zw
 * @date 2019/9/6
 */
package com.hp.sh.expv3.match.match.core.matched.task;

import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.bo.BbOrderSnapshotBo;
import com.hp.sh.expv3.match.config.setting.BbmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.BbmatchConst;
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
public class BbMatchedBookSnapshotTask extends BbMatchedBaseTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<BbOrder4MatchBo> limitAskOrders;
    private List<BbOrder4MatchBo> limitBidOrders;

    private BigDecimal lastPrice;

    public List<BbOrder4MatchBo> getLimitAskOrders() {
        return limitAskOrders;
    }

    public void setLimitAskOrders(List<BbOrder4MatchBo> limitAskOrders) {
        this.limitAskOrders = limitAskOrders;
    }

    public List<BbOrder4MatchBo> getLimitBidOrders() {
        return limitBidOrders;
    }

    public void setLimitBidOrders(List<BbOrder4MatchBo> limitBidOrders) {
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
    private BbmatchRedisKeySetting bbmatchRedisKeySetting;

    @Autowired
    @Qualifier(BbmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil bbRedisUtil;

    @Override
    public void run() {

        BbOrderSnapshotBo snapshot = new BbOrderSnapshotBo();
        snapshot.setLastPrice(lastPrice);
        snapshot.setLimitAskOrders(limitAskOrders);
        snapshot.setLimitBidOrders(limitBidOrders);
        long rmqNextOffset = this.getCurrentMsgOffset() + 1;
        snapshot.setRmqNextOffset(rmqNextOffset);

        String snapshotRedisKey = RedisKeyUtil.buildBbOrderSnapshotRedisKey(bbmatchRedisKeySetting.getBbOrderSnapshotRedisKeyPattern(), this.getAsset(), this.getSymbol());

        bbRedisUtil.hset(snapshotRedisKey, "" + rmqNextOffset, JsonUtil.toJsonString(snapshot));

        logger.info("save snapshot at {}", System.currentTimeMillis());

        updateSentMqOffset();
    }
}