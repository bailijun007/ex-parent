/**
 * @author 10086
 * @date 2019/10/22
 */
package com.hp.sh.expv3.match.mqmsg;

public class BbMatchOrderSnapshotMqMsgDto {

    private String asset;
    private String symbol;

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
}