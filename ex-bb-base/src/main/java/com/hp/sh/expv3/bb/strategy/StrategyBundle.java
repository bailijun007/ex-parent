package com.hp.sh.expv3.bb.strategy;

public class StrategyBundle {
	
	private final Integer strategyId;
	
	private final HoldPosStrategy holdPosStrategy;

	public StrategyBundle(int strategyId, HoldPosStrategy holdPosStrategy) {
		this.strategyId = strategyId;
		this.holdPosStrategy = holdPosStrategy;
	}

	public Integer strategyId() {
		return strategyId;
	}

	public HoldPosStrategy getHoldPosStrategy() {
		return holdPosStrategy;
	}

}
