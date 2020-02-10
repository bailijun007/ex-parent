package com.hp.sh.expv3.bb.module.order.vo;

import com.hp.sh.expv3.bb.constant.OrderFlag;

/**
 * BB交易对
 * @author wangjg
 *
 */
public class BBSymbol {
	
	private Integer bidFlag;

	//结算货币
	private String settleCurrency;

	//基础货币
	private String baseCurrency;

	public BBSymbol() {
		super();
	}

	public BBSymbol(String symbol) {
		String[] ss = symbol.split("_");
		this.settleCurrency = ss[1];
		this.baseCurrency = ss[0];
	}

	public BBSymbol(String symbol, Integer bidFlag) {
		String[] ss = symbol.split("_");
		this.settleCurrency = ss[1];
		this.baseCurrency = ss[0];
		this.bidFlag = bidFlag;
	}

	public BBSymbol(String settleCurrency, String baseCurrency) {
		super();
		this.settleCurrency = settleCurrency;
		this.baseCurrency = baseCurrency;
	}

	public String getSettleCurrency() {
		return settleCurrency;
	}

	public void setSettleCurrency(String settleCurrency) {
		this.settleCurrency = settleCurrency;
	}

	public Integer getBidFlag() {
		return bidFlag;
	}

	public void setBidFlag(Integer bidFlag) {
		this.bidFlag = bidFlag;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public String getIncomeCurrency() {
		if(this.bidFlag!=OrderFlag.BID_BUY){
			return this.settleCurrency;
		}else{
			return this.baseCurrency;
		}
	}

	public String getPayCurrency() {
		if(this.bidFlag==OrderFlag.BID_BUY){
			return this.settleCurrency;
		}else{
			return this.baseCurrency;
		}
	}

}
