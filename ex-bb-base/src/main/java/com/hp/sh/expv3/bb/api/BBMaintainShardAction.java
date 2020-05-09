package com.hp.sh.expv3.bb.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.bb.module.sys.service.DbGlobalService;
import com.hp.sh.expv3.config.shard.ShardGroup;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/bb/maintain2")
public class BBMaintainShardAction{
	private static final Logger logger = LoggerFactory.getLogger(BBMaintainShardAction.class);

	@Autowired
	private ShardGroup shardGroup;
	
	@Autowired
	private DbGlobalService dbGlobalService;

	@ApiOperation(value = "createNextMonthTables")
	@GetMapping(value = "/createNextMonthTables")
	public Long createNextMonthTables(){
		dbGlobalService.createNextMonthTables();
		return 0L;
	}
	
	@ApiOperation(value = "createCurMonthTables")
	@GetMapping(value = "/createCurMonthTables")
	public Long createCurMonthTables(){
		dbGlobalService.createCurMonthTables();
		return 0L;
	}

	@ApiOperation(value = "createNewSymbol")
	@GetMapping(value = "/createNewSymbol")
	public Long createNewSymbol(String asset, String symbol){
		dbGlobalService.createNewSymbol(asset, symbol);
		return 0L;
	}
	
	@ApiOperation(value = "userShard")
	@GetMapping(value = "/userShard")
	public Long userShard(Long userId){
		Long shardId = shardGroup.getMsgSardId(userId);
		return shardId;
	}

}
