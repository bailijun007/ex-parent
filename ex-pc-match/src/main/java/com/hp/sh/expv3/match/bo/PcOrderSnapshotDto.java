/**
 * @author 10086
 * @date 2019/10/24
 */
package com.hp.sh.expv3.match.bo;

import java.util.List;

public class PcOrderSnapshotDto {

    private long mqOffset;
    private List<PcOrder4MatchBo> limitAskQueue;
    private List<PcOrder4MatchBo> limitBidQueue;

    public PcOrderSnapshotDto(long mqOffset, List<PcOrder4MatchBo> limitAskQueue, List<PcOrder4MatchBo> limitBidQueue) {
        this.mqOffset = mqOffset;
        this.limitAskQueue = limitAskQueue;
        this.limitBidQueue = limitBidQueue;
    }

    public PcOrderSnapshotDto() {
    }

    public long getMqOffset() {
        return mqOffset;
    }

    public void setMqOffset(long mqOffset) {
        this.mqOffset = mqOffset;
    }

    public List<PcOrder4MatchBo> getLimitAskQueue() {
        return limitAskQueue;
    }

    public void setLimitAskQueue(List<PcOrder4MatchBo> limitAskQueue) {
        this.limitAskQueue = limitAskQueue;
    }

    public List<PcOrder4MatchBo> getLimitBidQueue() {
        return limitBidQueue;
    }

    public void setLimitBidQueue(List<PcOrder4MatchBo> limitBidQueue) {
        this.limitBidQueue = limitBidQueue;
    }
}
