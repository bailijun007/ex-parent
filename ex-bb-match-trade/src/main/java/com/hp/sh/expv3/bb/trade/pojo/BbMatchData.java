package com.hp.sh.expv3.bb.trade.pojo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/4/27
 */
public class BbMatchData {

    private String asset;

    private String symbol;

    private List<BbMatchExtVo> match;

    public BbMatchData() {
    }

    @Override
    public String toString() {
        return "BbMatchData{" +
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

    public List<BbMatchExtVo> getMatch() {
        return match;
    }

    public void setMatch(List<BbMatchExtVo> match) {
        this.match = match;
    }
}
