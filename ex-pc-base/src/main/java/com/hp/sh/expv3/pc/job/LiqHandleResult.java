package com.hp.sh.expv3.pc.job;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.vo.response.MarkPriceVo;

public class LiqHandleResult {
	
	//触发强平
	private boolean trigger = false;
	
    // 强平价
    private BigDecimal liqPrice;
	
	private MarkPriceVo markPriceVo;
	
	private PcPosition pcPosition;

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

	public PcPosition getPcPosition() {
		return pcPosition;
	}

	public void setPcPosition(PcPosition pcPosition) {
		this.pcPosition = pcPosition;
	}
    
}
