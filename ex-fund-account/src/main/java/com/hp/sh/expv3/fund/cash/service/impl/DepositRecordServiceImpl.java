package com.hp.sh.expv3.fund.cash.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.fund.cash.dao.DepositRecordDAO;
import com.hp.sh.expv3.fund.cash.entity.DepositRecord;
import com.hp.sh.expv3.fund.cash.service.DepositRecordService;
import com.hp.sh.expv3.fund.wallet.constant.Paystatus;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class DepositRecordServiceImpl implements DepositRecordService{
	private static final Logger logger = LoggerFactory.getLogger(DepositRecordServiceImpl.class);

	@Autowired
	private DepositRecordDAO depositRecordDAO;

	@Override
	public void save(DepositRecord depositRecord){
		Long now = DbDateUtils.now();
		if(depositRecord.getId()==null){
			depositRecord.setCreated(now);
			depositRecord.setModified(now);
			this.depositRecordDAO.save(depositRecord);
		}else{
			depositRecord.setModified(now);
			this.depositRecordDAO.update(depositRecord);
		}
	}

	@Override
	public void update(DepositRecord depositRecord){
		Long now = DbDateUtils.now();
		depositRecord.setModified(now);
		this.depositRecordDAO.update(depositRecord);
	}

	@Override
	public List<DepositRecord> pageQuery(Page page, Long userId, Integer status) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("userId", userId);
		params.put("status", status);
		List<DepositRecord> list = this.depositRecordDAO.queryList(params);
		return list;
	}

	@Override
	public List<DepositRecord> pageQuery(Page page, Long userId, Integer type, Integer channelId, Integer status, Long beginTime, Long endTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("userId", userId);
		params.put("type", type);
		params.put("channelId", channelId);
		params.put("status", status);
		
		params.put("createdBegin", beginTime);
		params.put("createdEnd", endTime);
		List<DepositRecord> list = this.depositRecordDAO.queryList(params);
		return list;
	}

	@Override
	public DepositRecord findBySn(Long userId, String sn){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("sn", sn);
		
		DepositRecord r = this.depositRecordDAO.queryOne(params);
		return r;
	}

	@Override
	public DepositRecord findById(Long userId, Long id){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("id", id);
		
		DepositRecord r = this.depositRecordDAO.queryOne(params);
		return r;
	}

	@Override
	public List<DepositRecord> findPending(Page page) {
		List<DepositRecord> list = this.pageQuery(page, null, Paystatus.PENDING);
		return list;
	}

	@Override
	public List<DepositRecord> findPaySuccess(Page page) {
		List<DepositRecord> list = this.pageQuery(page, null, Paystatus.SUCCESS);
		return list;
	}

}
