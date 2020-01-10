/**
 * @author 10086
 * @date 2019/10/24
 */
package com.hp.sh.expv3.match.bo;

import java.util.List;

public class BbOrderSnapshotDto {

    private long mqOffset;
    private List<BbOrder4MatchBo> limitAskQueue;
    private List<BbOrder4MatchBo> limitBidQueue;

    public BbOrderSnapshotDto(long mqOffset, List<BbOrder4MatchBo> limitAskQueue, List<BbOrder4MatchBo> limitBidQueue) {
        this.mqOffset = mqOffset;
        this.limitAskQueue = limitAskQueue;
        this.limitBidQueue = limitBidQueue;
    }

    public BbOrderSnapshotDto() {
    }

    public long getMqOffset() {
        return mqOffset;
    }

    public void setMqOffset(long mqOffset) {
        this.mqOffset = mqOffset;
    }

    public List<BbOrder4MatchBo> getLimitAskQueue() {
        return limitAskQueue;
    }

    public void setLimitAskQueue(List<BbOrder4MatchBo> limitAskQueue) {
        this.limitAskQueue = limitAskQueue;
    }

    public List<BbOrder4MatchBo> getLimitBidQueue() {
        return limitBidQueue;
    }

    public void setLimitBidQueue(List<BbOrder4MatchBo> limitBidQueue) {
        this.limitBidQueue = limitBidQueue;
    }
}
