package com.hp.sh.expv3.pc.strategy;

public class StrategyBundle {
	
	private String strategyId;
	
	private final PositionStrategy positionStrategy;
	
	private final HoldPosStrategy holdPosStrategy;

	public StrategyBundle(String strategyId, PositionStrategy positionStrategy, HoldPosStrategy holdPosStrategy) {
		this.strategyId = strategyId;
		this.positionStrategy = positionStrategy;
		this.holdPosStrategy = holdPosStrategy;
	}

	public String strategyId() {
		return strategyId;
	}

	public PositionStrategy getPositionStrategy() {
		return positionStrategy;
	}

	public HoldPosStrategy getHoldPosStrategy() {
		return holdPosStrategy;
	}

}
