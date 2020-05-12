package com.hp.sh.expv3.bb.module.sys.service;

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
import com.hp.sh.expv3.bb.component.MetadataService;
import com.hp.sh.expv3.bb.component.vo.BBSymbolVO;
import com.hp.sh.expv3.bb.module.sys.constant.LogicTable;
import com.hp.sh.expv3.bb.module.sys.dao.DbGlobalDAO;
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
		this.physicsTabls.addAll(arList);
		this.physicsTabls.addAll(ohList);
		this.physicsTabls.addAll(otList);
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
		List<String> symbolList = new ArrayList<String>();
		List<String> assetList = new ArrayList<>();
		
		List<BBSymbolVO> symbolVoList = metadataService.getAllBBSymbol();
		
		for(BBSymbolVO symbolVo : symbolVoList){
			symbolList.add(symbolVo.getSymbol());
			assetList.add(this.getAsset(symbolVo.getSymbol()));
		}
		
		assetList.add("USDT");
		
		for(String fullSymbol:symbolList){
			String[] sa = fullSymbol.split("__");
			String asset = sa[0];
			String symbol = sa[1];
			this.createOrderTable(asset, symbol, start, end);
			this.createOrderTradeTable(asset, symbol, start, end);
		}
		
		for(String asset:assetList){
			this.createAccountRecordTable(asset, start, end);
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
		
		this.createAccountRecordTable(this.getAsset(asset+"__"+symbol), start.getTime(), nextMonth.getTime());
		
		this.createOrderTable(asset, symbol, start.getTime(), nextMonth.getTime());
		this.createOrderTradeTable(asset, symbol, start.getTime(), nextMonth.getTime());
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
	
	private boolean exist(String table){
		return this.physicsTabls.contains(table);
	}
	
	private String getAsset(String symbol){
		String[] sa2 = symbol.split("_");
		String asset = sa2[0];
		return asset;
	}

//	@PostConstruct
	public void initSql(){
		try {
			FileConfig fc = new FileConfig();
			fc.setConfigPath("bb_ddl.sql");
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
