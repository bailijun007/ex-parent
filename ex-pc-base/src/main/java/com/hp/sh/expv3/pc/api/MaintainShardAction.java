package com.hp.sh.expv3.pc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.config.shard.ShardGroup;
import com.hp.sh.expv3.pc.module.sys.service.ShardTableService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/bb/maintain2")
public class MaintainShardAction{
	private static final Logger logger = LoggerFactory.getLogger(MaintainShardAction.class);

	@Autowired
	private ShardGroup shardGroup;
	
	@Autowired
	private ShardTableService shardTableService;

	@ApiOperation(value = "createNextMonthTables")
	@GetMapping(value = "/createNextMonthTables")
	public Long createNextMonthTables(){
		shardTableService.createNextMonthTables();
		shardTableService.loadPhysicsTableNames();
		return 0L;
	}
	
	@ApiOperation(value = "createCurMonthTables")
	@GetMapping(value = "/createCurMonthTables")
	public Long createCurMonthTables(){
		shardTableService.createCurMonthTables();
		shardTableService.loadPhysicsTableNames();
		return 0L;
	}

	@ApiOperation(value = "createNewSymbol")
	@GetMapping(value = "/createNewSymbol")
	public Long createNewSymbol(String asset, String symbol){
		shardTableService.createNewSymbol(asset, symbol);
		shardTableService.loadPhysicsTableNames();
		return 0L;
	}
	
	@ApiOperation(value = "userShard")
	@GetMapping(value = "/userShard")
	public Long userShard(Long userId){
		Long shardId = shardGroup.getMsgSardId(userId);
		return shardId;
	}

}
