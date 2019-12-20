package com.hp.sh.expv3.pc.strategy.aabb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AABBBundle {
	
	@Autowired
	private AABBMetadataService metadataService;
	
	@Autowired
	private AABBPositionStrategy positionStrategy;
	
	@Autowired
	private AABBHoldPosStrategy holdPosStrategy;

	public AABBMetadataService getMetadataService() {
		return metadataService;
	}

	public void setMetadataService(AABBMetadataService metadataService) {
		this.metadataService = metadataService;
	}

	public AABBPositionStrategy getPositionStrategy() {
		return positionStrategy;
	}

	public void setPositionStrategy(AABBPositionStrategy positionStrategy) {
		this.positionStrategy = positionStrategy;
	}

	public AABBHoldPosStrategy getHoldPosStrategy() {
		return holdPosStrategy;
	}

	public void setHoldPosStrategy(AABBHoldPosStrategy holdPosStrategy) {
		this.holdPosStrategy = holdPosStrategy;
	}
	
	public String getStrategyId(){
		return "AABB";
	}

}
