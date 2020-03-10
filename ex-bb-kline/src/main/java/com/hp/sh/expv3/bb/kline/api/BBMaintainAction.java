package com.hp.sh.expv3.bb.kline.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
public class BBMaintainAction{

	@ApiOperation(value = "version")
	@GetMapping(value = "/api/bb/maintain/version")
	public Integer version(){
		return 1001;
	}
}
