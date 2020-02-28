package com.hp.sh.expv3.bb.strategy.vo;

public class BBTradePair {

	private BBTradeVo makerTradeVo;
	
	private BBTradeVo takerTradeVo;

	public BBTradePair(BBTradeVo makerTradeVo, BBTradeVo takerTradeVo) {
		this.makerTradeVo = makerTradeVo;
		this.takerTradeVo = takerTradeVo;
	}

	public BBTradeVo getMakerTradeVo() {
		return makerTradeVo;
	}

	public void setMakerTradeVo(BBTradeVo makerTradeVo) {
		this.makerTradeVo = makerTradeVo;
	}

	public BBTradeVo getTakerTradeVo() {
		return takerTradeVo;
	}

	public void setTakerTradeVo(BBTradeVo takerTradeVo) {
		this.takerTradeVo = takerTradeVo;
	}

}
