package com.hp.sh.expv3.fund.transfer.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.fund.transfer.dao.FundTransferDAO;
import com.hp.sh.expv3.fund.transfer.entity.FundTransfer;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.SnUtils;

/**
 * 资金划转
 * @author lw
 *
 */
@Service
@Transactional
public class FundTransferCoreService {
	
	@Autowired
	private FundTransferDAO fundTransferDAO;

	/**
	 * 转给自己
	 * @param asset
	 * @param srcAccountType
	 * @param targetAccountType
	 * @param amount
	 */
	public FundTransfer transfer(Long userId, String asset, Integer srcAccountType, Integer targetAccountType, BigDecimal amount) {
		Long now = DbDateUtils.now();
		
		FundTransfer transfer = new FundTransfer();
		transfer.setSn(SnUtils.newTransferSn());
		transfer.setAsset(asset);
		transfer.setAmount(amount);
		transfer.setSrcAccountType(srcAccountType);
		transfer.setTargetAccountType(targetAccountType);
		transfer.setSrcAccountId(userId);
		transfer.setTargetAccountId(userId);
		transfer.setRemark("资金划转");
		transfer.setUserId(userId);
		transfer.setCreated(now);
		transfer.setModified(now);
		transfer.setStatus(FundTransfer.STATUS_NEW);
		
		fundTransferDAO.save(transfer);
		
		return transfer;
	}
	
	public void changeStatus(FundTransfer tr, Integer newStatsus, String errorInfo){
		this.changeStatus(tr.getUserId(), tr.getId(), newStatsus, tr.getStatus(), errorInfo, DbDateUtils.now());
		tr.setStatus(newStatsus);
	}
	
	void changeStatus(Long userId, Long id, Integer newStatsus, Integer oldStatsus, String errorInfo, Long modified) {
		int n = this.fundTransferDAO.changeStatus(userId, id, newStatsus, oldStatsus, errorInfo, modified);
		if(n==0){
			throw new RuntimeException("更新失败");
		}
	}

	public List<FundTransfer> findPending(Page page) {
		List<FundTransfer> list = this.fundTransferDAO.queryPending(page, FundTransfer.STATUS_SUCCESS, FundTransfer.STATUS_FAIL);
		return list;
	}

	public FundTransfer findById(Long userId, Long id) {
		return this.fundTransferDAO.findById(userId, id);
	}
	
}
