package com.hp.sh.expv3.pc.strategy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PositionStrategyContext {

	@Autowired
	private List<PositionStrategy> list;
	
	
	
	
}
