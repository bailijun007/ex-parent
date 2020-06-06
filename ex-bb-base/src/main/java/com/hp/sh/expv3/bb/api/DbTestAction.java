package com.hp.sh.expv3.bb.api;

import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.json.JsonUtils;
import com.hp.sh.expv3.bb.job.MsgShardHandler;
import com.hp.sh.expv3.bb.module.fail.dao.BBMqMsgDAO;
import com.hp.sh.expv3.bb.module.fail.entity.BBMqMsg;
import com.hp.sh.expv3.bb.module.fail.service.BBMqMsgService;
import com.hp.sh.expv3.bb.mq.listen.mq.MatchMqHandler;
import com.hp.sh.expv3.bb.mq.msg.in.BBTradeMsg;
import com.hp.sh.expv3.component.lock.TxIdService;
import com.hp.sh.expv3.utils.DbDateUtils;

import io.swagger.annotations.ApiOperation;

@RestController
public class DbTestAction {

	@Autowired
	private BBMqMsgService msgService;
	
	@Autowired
	private BBMqMsgDAO bBMqMsgDAO;
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private MsgShardHandler msgHandler;
	
	@Autowired(required=false)
	private TxIdService txIdService;
	
	@Autowired
	private MatchMqHandler mqHandler;
	
	@Autowired
	private DbTestService dbTestService;
	
	@Autowired
	private DataSource dataSource;
	
	private static final long RECORD_TOTAL = 100000;
	
	long idseq = 0;

	@ApiOperation(value = "测试Lock")
	@GetMapping(value = "/api/bb/test/lock")
	public Long testLock(){
		long time = System.currentTimeMillis();
		for(long i=0; i<RECORD_TOTAL; i++){
			mqHandler.testLock(i);
		}
		time = System.currentTimeMillis()-time;
		return time;
	}

	@ApiOperation(value = "测试保存数据库（单线程多事务）")
	@GetMapping(value = "/api/bb/test/dbsave1")
	public Long dbsave1(){
		long time = System.currentTimeMillis();
		this.doDbsave(RECORD_TOTAL);
		time = System.currentTimeMillis()-time;
		return time;
	}

	@ApiOperation(value = "测试保存数据库（单线程单事务）")
	@GetMapping(value = "/api/bb/test/dbsave2")
	@Transactional(rollbackFor=Exception.class)
	public Long dbsave2(){
		long time = System.currentTimeMillis();
		this.doDbsave(RECORD_TOTAL);
		time = System.currentTimeMillis()-time;
		return time;
	}
	
	@ApiOperation(value = "测试多线程保存数据库（多线程多事务）")
	@GetMapping(value = "/api/bb/test/dbsavemt")
	public Long dbsavemt() throws InterruptedException{
		String tag="test";
		String ext="test";
		
		long time = System.currentTimeMillis();
		final int threadNum = 100;
		final long msgNumPerThread = RECORD_TOTAL/threadNum;
		Thread[] ta = new Thread[threadNum];
		for(int i=0;i<threadNum;i++){
			Thread t = new Thread(){
				public void run(){
					doDbsave(msgNumPerThread);
				}
			};
			t.start();
			ta[i] = t;
		}
		
		for(int i=0;i<threadNum;i++){
			ta[i].join();
		}
		
		time = System.currentTimeMillis()-time;
		return time;
	}
	
	private void doDbsave(long msgNum){
		String tag="test";
		String ext="test";
		
		for(long i=0;i<msgNum;i++){
			BBTradeMsg msg = getBBTradeMsg();
			BBMqMsg entity = this.getBBMqMsg(msg);
			dbTestService.save4(entity);
		}
	}

	@ApiOperation(value = "测试保存文件")
	@GetMapping(value = "/api/bb/test/filesave")
	public Long filesave() throws IOException{
		String tag="test";
		String ext="test";
		FileWriter out = new FileWriter("e:\\wal.txt", true);
		long time = System.currentTimeMillis();
		
		BBTradeMsg msg = getBBTradeMsg();
		BBMqMsg msgEntity = this.getBBMqMsg(msg);
		String json = JsonUtils.toJson(msgEntity);
		
		for(int i=0; i<RECORD_TOTAL; i++){
			out.write(json);
			out.write('\n');
			out.flush();
		}
		out.close();
		time = System.currentTimeMillis()-time;
		return time;
	}

	@ApiOperation(value = "测试保存文件(随机)")
	@GetMapping(value = "/api/bb/test/filesave2")
	public Long filesave2() throws IOException{
		String tag="test";
		String ext="test";
		RandomAccessFile out = new RandomAccessFile("e:\\wal.txt", "rw");
		long time = System.currentTimeMillis();
		
		BBTradeMsg msg = getBBTradeMsg();
		BBMqMsg msgEntity = this.getBBMqMsg(msg);
		String json = JsonUtils.toJson(msgEntity);
		byte[] buf = json.getBytes();
		for(int i=0; i<RECORD_TOTAL*1000; i++){
			out.write(buf);
			out.write('\n');
//			System.out.println(out.getFilePointer());
		}
		out.close();
		time = System.currentTimeMillis()-time;
		return time;
	}
	
	private BBMqMsg getBBMqMsg(BBTradeMsg msg){
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
		
		msgEntity.setId(txIdService.getTxId());
		
		return msgEntity;
	}

	private BBTradeMsg getBBTradeMsg(){
		BBTradeMsg msg = new BBTradeMsg();
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
	
	private void startExitThread(){
		new ExitThread().start();
	}
	
	static class ExitThread extends Thread{
		public void run(){
			System.exit(-1);
		}
	}
	
}
