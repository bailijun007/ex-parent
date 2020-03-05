package com.hp.sh.expv3.fund.cash.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.fund.cash.job.pay.WithdrawalPayJob;

import io.swagger.annotations.ApiOperation;

@RestController
public class MaintainAction{

	@Autowired
	private WithdrawalPayJob withdrawalJob;

	@ApiOperation(value = "job")
	@GetMapping(value = "/api/fund/maintain/job/handle")	
	public void jobHandle(){
		withdrawalJob.handlePendingWithDrawal();
	}
}
