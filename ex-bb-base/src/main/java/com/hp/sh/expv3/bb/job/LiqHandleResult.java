package com.hp.sh.expv3.bb.job;

import java.math.BigDecimal;

import com.hp.sh.expv3.bb.module.position.entity.BBPosition;
import com.hp.sh.expv3.bb.vo.response.MarkPriceVo;

public class LiqHandleResult {
	
	//触发强平
	private boolean trigger = false;
	
    // 强平价
    private BigDecimal liqPrice;
	
	private MarkPriceVo markPriceVo;
	
	private BBPosition bBPosition;

	public boolean isTrigger() {
		return trigger;
	}

	public void setTrigger(boolean trigger) {
		this.trigger = trigger;
	}

	public BigDecimal getLiqPrice() {
		return liqPrice;
	}

	public void setLiqPrice(BigDecimal liqPrice) {
		this.liqPrice = liqPrice;
	}

	public MarkPriceVo getMarkPriceVo() {
		return markPriceVo;
	}

	public void setMarkPriceVo(MarkPriceVo markPriceVo) {
		this.markPriceVo = markPriceVo;
	}

	public BBPosition getPcPosition() {
		return bBPosition;
	}

	public void setPcPosition(BBPosition bBPosition) {
		this.bBPosition = bBPosition;
	}
    
}
