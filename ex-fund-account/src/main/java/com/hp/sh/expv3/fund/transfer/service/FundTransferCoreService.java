package com.hp.sh.expv3.fund.transfer.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.fund.transfer.dao.FundTransferDAO;
import com.hp.sh.expv3.fund.transfer.entity.FundTransfer;
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
	public void transfer(Long userId, String asset, Integer srcAccountType, Integer targetAccountType, BigDecimal amount) {
		Date now = new Date();
		
		FundTransfer transfer = new FundTransfer();
		transfer.setSn(SnUtils.genTransferSn());
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
		
	}
	
	public void changeStatus(FundTransfer tr, Integer newStatsus){
		this.changeStatus(tr.getUserId(), tr.getId(), newStatsus, tr.getStatus(), new Date());
		tr.setStatus(newStatsus);
	}
	
	public void changeStatus(Long userId, Long id, Integer newStatsus, Integer oldStatsus, Date modified) {
		this.fundTransferDAO.changeStatus(userId, id, newStatsus, oldStatsus, modified);
	}

	public List<FundTransfer> findPending(Page page) {
		List<FundTransfer> list = this.fundTransferDAO.queryPending(page);
		return list;
	}
	
}
