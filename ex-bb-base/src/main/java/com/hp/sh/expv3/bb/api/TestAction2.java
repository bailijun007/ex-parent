package com.hp.sh.expv3.bb.api;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.json.JsonUtils;
import com.hp.sh.expv3.bb.job.MsgShardHandler;
import com.hp.sh.expv3.bb.module.fail.entity.BBMqMsg;
import com.hp.sh.expv3.bb.module.fail.service.BBMqMsgService;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageOffsetService;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.utils.DbDateUtils;

import io.swagger.annotations.ApiOperation;

@RestController
public class TestAction2 {

	@Autowired
	private BBMqMsgService msgService;
	
	@Autowired
	private MsgShardHandler msgHandler;
	
	@Autowired
	private BBMessageOffsetService offsetService;
	
	@ApiOperation(value = "测试cache")
	@GetMapping(value = "/api/bb/test/cache")
	public Long cache() throws Exception{
		Long id = offsetService.cacheShardOffset(1L, 100L);
		offsetService.getCachedShardOffset(1L);
		offsetService.getCachedShardOffset(1L);
		return id;
	}

	@ApiOperation(value = "测试保存数据库（单线程多事务）")
	@GetMapping(value = "/api/bb/test/dbsave1")
	public Long dbsave1(){
		long time = System.currentTimeMillis();
		this.doDbsave();
		time = System.currentTimeMillis()-time;
		return time;
	}

	@ApiOperation(value = "测试保存数据库（单线程单事务）")
	@GetMapping(value = "/api/bb/test/dbsave2")
	@Transactional(rollbackFor=Exception.class)
	public Long dbsave2(){
		long time = System.currentTimeMillis();
		this.doDbsave();
		time = System.currentTimeMillis()-time;
		return time;
	}
	
	private void doDbsave(){
		String tag="test";
		String ext="test";
		
		for(int i=0;i<100000;i++){
			BBTradeVo msg = getBBTradeVo();
			msgService.save(tag, msg, ext);
		}
	}
	
	@ApiOperation(value = "测试多线程保存数据库（多线程多事务）")
	@GetMapping(value = "/api/bb/test/dbsavemt")
	public Long dbsavemt() throws InterruptedException{
		String tag="test";
		String ext="test";
		
		BBTradeVo msg = getBBTradeVo();
		long time = System.currentTimeMillis();
		Thread[] ta = new Thread[100];
		for(int i=0;i<100;i++){
			Thread t = new Thread(){
				public void run(){
					for(int i=0;i<1000;i++){
						msgService.save(tag, msg, ext);
					}
				}
			};
			t.start();
			ta[i] = t;
		}
		
		for(int i=0;i<100;i++){
			ta[i].join();
		}
		
		time = System.currentTimeMillis()-time;
		return time;
	}

	@ApiOperation(value = "测试保存文件")
	@GetMapping(value = "/api/bb/test/filesave")
	public Long filesave() throws IOException{
		String tag="test";
		String ext="test";
		FileWriter out = new FileWriter("e:\\wal.txt", true);
		long time = System.currentTimeMillis();
		for(int i=0;i<10000;i++){
			BBTradeVo msg = getBBTradeVo();
			BBMqMsg msgEntity = this.getBBMqMsg(msg);
			String json = JsonUtils.toJson(msgEntity);
			out.write(json);
			out.write('\n');
		}
		out.close();
		time = System.currentTimeMillis()-time;
		return time;
	}
	
	@ApiOperation(value = "测试提交任务")
	@GetMapping(value = "/api/bb/test/submitTask")
	public Long submitTask(Long shardId) throws Exception{
		long time = System.currentTimeMillis();
		msgHandler.handlePending(shardId);
		time = System.currentTimeMillis()-time;
		return time;
	}
	
	@ApiOperation(value = "任务执行结果")
	@GetMapping(value = "/api/bb/test/taskResult")
	public Long taskResult() throws IOException{
		return 0L;
	}
	
	private BBMqMsg getBBMqMsg(BBTradeVo msg){
		BBMqMsg msgEntity = new BBMqMsg();
		msgEntity.setUserId(msg.getAccountId());
		
		msgEntity.setAsset(msg.getAsset());
		msgEntity.setSymbol(msg.getSymbol());
		msgEntity.setExMessage("exMessage");
		
		msgEntity.setMessageId(msg.getMsgId());
		msgEntity.setTag(msg.getTags());
		msgEntity.setKey(""+msg.getOrderId());
		msgEntity.setBody(JsonUtils.toJson(msg));
		
		msgEntity.setCreated(DbDateUtils.now());
		msgEntity.setSortId(1L);
		return msgEntity;
	}

	private BBTradeVo getBBTradeVo(){
		BBTradeVo msg = new BBTradeVo();
		msg.setAccountId(1L);
		msg.setAsset("USDT");
		msg.setKeys("1234567890");
		msg.setMakerFlag(0);
		msg.setMatchTxId(1L);
		msg.setMsgId("abcdef");
		msg.setNumber(BigDecimal.TEN);
		msg.setOpponentOrderId(1L);
		msg.setOrderId(1L);
		msg.setPrice(BigDecimal.TEN);
		msg.setSymbol("BTC_USDT");
		msg.setTags("test");
		msg.setTopic("xxx");
		msg.setTradeId(1L);
		msg.setTradeTime(1L);
		return msg;
	}
	
}
