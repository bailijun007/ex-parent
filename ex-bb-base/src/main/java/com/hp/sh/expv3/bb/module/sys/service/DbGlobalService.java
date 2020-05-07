package com.hp.sh.expv3.bb.module.sys.service;

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
	
	public void createNextMonthTables(){
		Date date = DateUtils.monthEnd(new Date());
		Date nextMonth = DateUtils.dayAdd(date, 1);
		logger.info("nextMonth={}", nextMonth.toLocaleString());
		String asset = "USDT";
		String[] symbols = {"BTC_USDT", "BYS_USDT", "ETC_USDT", "ETH_USDT", "LTC_USDT"};
		
		for(String symbol:symbols){
			this.createOrderTable(asset, symbol, nextMonth.getTime(), nextMonth.getTime());
		}
	}
	
	public void createAccountRecordTable(String asset, Long start, Long end){
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
	
	public void createOrderTable(String asset, String symbol, Long start, Long end){
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
	
	public void createOrderTradeTable(String asset, String symbol, Long start, Long end){
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
