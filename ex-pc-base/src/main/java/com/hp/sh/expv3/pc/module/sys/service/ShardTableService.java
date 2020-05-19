package com.hp.sh.expv3.pc.module.sys.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.date.DateUtils;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.component.vo.PcContractVO;
import com.hp.sh.expv3.pc.constant.LogicTable;
import com.hp.sh.expv3.pc.module.sys.dao.DbGlobalDAO;
import com.hp.sh.expv3.commons.config.FileConfig;
import com.hp.sh.expv3.component.dbshard.DateShardUtils;
import com.hp.sh.expv3.component.dbshard.TableShardingByDateAsset;
import com.hp.sh.expv3.component.dbshard.TableShardingByDateSymbol;

@Service
@Transactional(rollbackFor=Exception.class)
public class ShardTableService{
	private static final Logger logger = LoggerFactory.getLogger(ShardTableService.class);

	private static final String dbName = null;
	
	@Autowired
	private DbGlobalDAO dbGlobalDAO;
	
	@Autowired
	private MetadataService metadataService;

	private final Set<String> physicsTabls = new HashSet<>();
	
	public boolean have(String table){
		return physicsTabls.contains(table);
	}
	
	@PostConstruct
	public void loadPhysicsTableNames(){
		List<String> arList = this.dbGlobalDAO.findTableByKeyword(dbName, LogicTable.ACCOUNT_RECORD);
		List<String> ohList = this.dbGlobalDAO.findTableByKeyword(dbName, LogicTable.ORDER_HISTORY);
		List<String> otList = this.dbGlobalDAO.findTableByKeyword(dbName, LogicTable.ORDER_TRADE);
		List<String> posList = this.dbGlobalDAO.findTableByKeyword(dbName, LogicTable.POSITION_HISTORY);
		this.physicsTabls.addAll(arList);
		this.physicsTabls.addAll(ohList);
		this.physicsTabls.addAll(otList);
		this.physicsTabls.addAll(posList);
	}

	public void createCurMonthTables() {
		Date nextMonth = DateUtils.monthEnd(new Date());
		logger.info("nextMonth={}", nextMonth.toLocaleString());
		this.createAllTables(nextMonth.getTime(), nextMonth.getTime());
	}
	
	public void createNextMonthTables(){
		Date date = DateUtils.monthEnd(new Date());
		Date nextMonth = DateUtils.dayAdd(date, 1);
		logger.info("nextMonth={}", nextMonth.toLocaleString());
		this.createAllTables(nextMonth.getTime(), nextMonth.getTime());
	}
	
	public void createAllTables(Long start, Long end){
		logger.info("nextMonth={}", new Date(end).toLocaleString());
		Set<String> assetList = new HashSet<>();
		
		List<PcContractVO> contractList = metadataService.getAllPcContract();
		
		for(PcContractVO contract : contractList){
			String[] asa = contract.getSymbol().split("_");
			assetList.add(contract.getAsset());
			assetList.add(asa[0]);
			assetList.add(asa[1]);
		}
		
		for(String asset:assetList){
			this.createAccountRecordTable(asset, start, end);
		}
		
		for(PcContractVO contract : contractList){
			this.createOrderTable(contract.getAsset(), contract.getSymbol(), start, end);
			this.createOrderTradeTable(contract.getAsset(), contract.getSymbol(), start, end);
			this.createPositionTable(contract.getAsset(), contract.getSymbol(), start, end);
		}
	}
	
	public void createNewSymbol(String asset, String symbol){
		String firstTableName = this.dbGlobalDAO.findFirstTableByKeyword(dbName, LogicTable.ORDER_TRADE+"_");
		
		Date date = DateUtils.monthEnd(new Date());
		Date nextMonth = DateUtils.dayAdd(date, 1);
		
		Date start = nextMonth;
		if(firstTableName!=null){
			start = DateShardUtils.getTableDate(firstTableName);
		}
		
		logger.info("nextMonth={}", nextMonth.toLocaleString());
		
		this.createAccountRecordTable(asset, start.getTime(), nextMonth.getTime());
		
		this.createOrderTable(asset, symbol, start.getTime(), nextMonth.getTime());
		this.createOrderTradeTable(asset, symbol, start.getTime(), nextMonth.getTime());
		this.createPositionTable(asset, symbol, start.getTime(), nextMonth.getTime());
	}
	
	protected void createAccountRecordTable(String asset, Long start, Long end){
		List<String> list = DateShardUtils.getRangeDates(start, end);
		for(String date : list){
			String table = TableShardingByDateAsset.getTableName(LogicTable.ACCOUNT_RECORD, asset, date);
			if(!exist(table)){
				dbGlobalDAO.createAccountRecordTable(table);
			}else{
				logger.error("表已存在：{}", table);
			}
		}
	}
	
	protected void createOrderTable(String asset, String symbol, Long start, Long end){
		System.out.print(new Date(end).toLocaleString());
		List<String> list = DateShardUtils.getRangeDates(start, end);
		for(String date : list){
			String table = TableShardingByDateSymbol.getTableName(LogicTable.ORDER_HISTORY, asset, symbol, date);
			if(!exist(table)){
				dbGlobalDAO.createOrderTable(table);
			}else{
				logger.error("表已存在：{}", table);
			}
		}
	}
	
	protected void createOrderTradeTable(String asset, String symbol, Long start, Long end){
		List<String> list = DateShardUtils.getRangeDates(start, end);
		for(String date : list){
			String table = TableShardingByDateSymbol.getTableName(LogicTable.ORDER_TRADE, asset, symbol, date);
			if(!exist(table)){
				dbGlobalDAO.createOrderTradeTable(table);
			}else{
				logger.error("表已存在：{}", table);
			}
		}
	}
	
	protected void createPositionTable(String asset, String symbol, Long start, Long end){
		List<String> list = DateShardUtils.getRangeDates(start, end);
		for(String date : list){
			String table = TableShardingByDateSymbol.getTableName(LogicTable.POSITION_HISTORY, asset, symbol, date);
			if(!exist(table)){
				dbGlobalDAO.createPositionTable(table);
			}else{
				logger.error("表已存在：{}", table);
			}
		}
	}
	
	private boolean exist(String table){
		return this.physicsTabls.contains(table);
	}

//	@PostConstruct
	public void initSql(){
		try {
			FileConfig fc = new FileConfig();
			fc.setConfigPath("pc_ddl.sql");
			String text = fc.getFileContent();
			String[] sqls = text.split(";");
			for(String sql:sqls){
				this.dbGlobalDAO.execute(sql);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
