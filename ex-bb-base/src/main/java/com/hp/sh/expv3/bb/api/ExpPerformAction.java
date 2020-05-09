package com.hp.sh.expv3.bb.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.bb.job.MsgShardHandler;
import com.hp.sh.expv3.bb.module.msg.entity.BBMessageExt;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageExtService;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageOffsetService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/bb/test/perform")
public class ExpPerformAction {

	@Autowired
	private MsgShardHandler msgHandler;

	@Autowired
	private BBMessageExtService msgService;
	
	@Autowired
	private BBMessageOffsetService offsetService;
	
	@ApiOperation(value = "")
	@GetMapping(value = "/oneShard")
	public void handleOneShard(Long shardId) throws Exception{
		msgHandler.handlePending(shardId);
		return ;
	}
	
	@ApiOperation(value = "")
	@GetMapping(value = "/oneUser")
	public void handleOneUser(Long userId, Integer num) throws Exception{
		List<BBMessageExt> userMsgList = msgService.findFirstList(num, null, userId);
		msgHandler.handleBatch(userId, userMsgList);
		return ;
	}

	
}
