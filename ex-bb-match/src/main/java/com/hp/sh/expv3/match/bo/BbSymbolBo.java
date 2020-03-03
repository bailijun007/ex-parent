/**
 * @author zw
 * @date 2019/7/19
 */
package com.hp.sh.expv3.match.bo;

import java.io.Serializable;

/**
 * 现货_交易对信息
 */
public class BbSymbolBo implements Serializable {

    private String asset;
    private String symbol;
    private Integer bbGroupId;


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

    public Integer getBbGroupId() {
        return bbGroupId;
    }

    public void setBbGroupId(Integer bbGroupId) {
        this.bbGroupId = bbGroupId;
    }

}