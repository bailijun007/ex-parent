package com.hp.sh.expv3.pc.extension.sys.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.extension.constant.LogicTable;
import com.hp.sh.expv3.pc.extension.sys.dao.DbGlobalDAO;

@Service
@Transactional(rollbackFor=Exception.class)
public class ShardTableService{
	private static final Logger logger = LoggerFactory.getLogger(ShardTableService.class);

	private static final String dbName = null;
	
	@Autowired
	private DbGlobalDAO dbGlobalDAO;
	
	private final Set<String> physicsTabls = new HashSet<>();
	
	public boolean have(String table){
		return this.exist(table);
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

	private boolean exist(String table){
		return this.physicsTabls.contains(table);
	}

}
