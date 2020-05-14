package com.hp.sh.expv3.bb.strategy.vo;

import com.hp.sh.expv3.bb.mq.msg.in.BBTradeMsg;

public class BBTradePair {

	private BBTradeMsg makerTradeVo;
	
	private BBTradeMsg takerTradeVo;

	public BBTradePair(BBTradeMsg makerTradeVo, BBTradeMsg takerTradeVo) {
		this.makerTradeVo = makerTradeVo;
		this.takerTradeVo = takerTradeVo;
	}

	public BBTradeMsg getMakerTradeVo() {
		return makerTradeVo;
	}

	public void setMakerTradeVo(BBTradeMsg makerTradeVo) {
		this.makerTradeVo = makerTradeVo;
	}

	public BBTradeMsg getTakerTradeVo() {
		return takerTradeVo;
	}

	public void setTakerTradeVo(BBTradeMsg takerTradeVo) {
		this.takerTradeVo = takerTradeVo;
	}

}
