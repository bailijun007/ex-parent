package com.hp.sh.expv3.bb.module.sys.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.date.DateUtils;
import com.hp.sh.expv3.bb.module.sys.dao.DbGlobalDAO;
import com.hp.sh.expv3.component.dbshard.DateShardUtils;
import com.hp.sh.expv3.component.dbshard.TableShardingByDateAsset;
import com.hp.sh.expv3.component.dbshard.TableShardingByDateSymbol;

@Service
@Transactional(rollbackFor=Exception.class)
public class DbGlobalService {
	private static final Logger logger = LoggerFactory.getLogger(DbGlobalService.class);

	private static final String dbName = null;
	
	private static final String ACCOUNT_RECORD = "bb_account_record";
	private static final String ORDER = "bb_order";
	private static final String ORDER_TRADE = "bb_order_trade";
	
	@Autowired
	private DbGlobalDAO dbGlobalDAO;

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
		String[] fullSymbols = {"USDT__BTC_USDT","USDT__EOS_USDT","USDT__ETH_USDT","USDT__LTC_USDT","USDT__BCH_USDT","USDT__BSV_USDT","USDT__BYS_USDT","USDT__ETC_USDT","USDT__XRP_USDT"};

		List<String> assets = new ArrayList<>();
		for(String fullSymbol:fullSymbols){
			String asset = getAsset(fullSymbol);
			assets.add(asset);
		}
		assets.add("USDT");
		
		for(String fullSymbol:fullSymbols){
			String[] sa = fullSymbol.split("__");
			String asset = sa[0];
			String symbol = sa[1];
			this.createOrderTable(asset, symbol, start, end);
			this.createOrderTradeTable(asset, symbol, start, end);
		}
		
		for(String asset:assets){
			this.createAccountRecordTable(asset, start, end);
		}
	}
	
	private String getAsset(String fullSymbol){
		String[] sa = fullSymbol.split("__");
		String[] sa2 = sa[1].split("_");
		String asset = sa2[0];
		return asset;
	}
	
	public void createNewSymbol(String asset, String symbol){
		String firstTableName = this.dbGlobalDAO.findFirstTableByKeyword(dbName, ORDER_TRADE+"_");
		
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
			String table = TableShardingByDateAsset.getTableName(ACCOUNT_RECORD, asset, date);
			if(this.dbGlobalDAO.findTableName(dbName, table)==null){
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
			String table = TableShardingByDateSymbol.getTableName(ORDER, asset, symbol, date);
			if(this.dbGlobalDAO.findTableName(dbName, table)==null){
				dbGlobalDAO.createOrderTable(table);
			}else{
				logger.error("表已存在：{}", table);
			}
		}
	}
	
	protected void createOrderTradeTable(String asset, String symbol, Long start, Long end){
		List<String> list = DateShardUtils.getRangeDates(start, end);
		for(String date : list){
			String table = TableShardingByDateSymbol.getTableName(ORDER_TRADE, asset, symbol, date);
			if(this.dbGlobalDAO.findTableName(dbName, table)==null){
				dbGlobalDAO.createOrderTradeTable(table);
			}else{
				logger.error("表已存在：{}", table);
			}
		}
	}

}
