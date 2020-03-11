package com.hp.sh.expv3.pc.strategy;

public class StrategyBundle {
	
	private final Integer strategyId;
	
	private final HoldPosStrategy holdPosStrategy;
	
	private final OrderStrategy orderStrategy;

	public StrategyBundle(int strategyId, HoldPosStrategy holdPosStrategy, OrderStrategy orderStrategy) {
		this.strategyId = strategyId;
		this.holdPosStrategy = holdPosStrategy;
		this.orderStrategy = orderStrategy;
	}

	public Integer strategyId() {
		return strategyId;
	}

	public HoldPosStrategy getHoldPosStrategy() {
		return holdPosStrategy;
	}

	public OrderStrategy getOrderStrategy() {
		return orderStrategy;
	}

	@Override
	public String toString() {
		return "StrategyBundle [strategyId=" + strategyId + "]";
	}

}
