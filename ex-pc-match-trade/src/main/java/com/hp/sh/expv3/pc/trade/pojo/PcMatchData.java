package com.hp.sh.expv3.pc.trade.pojo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/4/27
 */
public class PcMatchData {

    private String asset;

    private String symbol;

    private List<PcMatchExtVo> match;

    public PcMatchData() {
    }

    @Override
    public String toString() {
        return "PcMatchData{" +
                "asset='" + asset + '\'' +
                ", symbol='" + symbol + '\'' +
                ", match=" + match +
                '}';
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

    public List<PcMatchExtVo> getMatch() {
        return match;
    }

    public void setMatch(List<PcMatchExtVo> match) {
        this.match = match;
    }
}
