/**
 * @author zw
 * @date 2019/7/19
 */
package com.hp.sh.expv3.match.bo;

import java.io.Serializable;

/**
 * 永续合约_交易对信息
 */
public class PcContractBo implements Serializable {

    private String asset;
    private String symbol;
    private Integer contractGroup;

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

    public Integer getContractGroup() {
        return contractGroup;
    }

    public void setContractGroup(Integer contractGroup) {
        this.contractGroup = contractGroup;
    }
}
