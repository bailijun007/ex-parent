package com.hp.sh.expv3.component.payment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ThirdPayServiceFactory {
	
	@Autowired(required=false)
	private List<ThirdPayService> payServiceList;

	public ThirdPayService get(String channel){
		for(ThirdPayService ps : payServiceList){
			if(ps.getName().equals(channel)){
				return ps;
			}
		}
		return null;
	}
	
}
