
package com.hp.sh.expv3.fund.cash.service;

import java.util.List;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.fund.cash.entity.DepositRecord;

/**
 * 
 * @author wangjg
 *
 */
public interface DepositRecordService {

	public void update(DepositRecord depositRecord);

	/**
	 * @param page
	 * @param userId
	 * @param status
	 * @return
	 */
	List<DepositRecord> pageQuery(Page page, Long userId, Integer status);

	/**
	 * @param page
	 * @param userId
	 * @param type
	 * @param channelId
	 * @param status
	 * @return
	 */
	public List<DepositRecord> pageQuery(Page page, Long userId, Integer type, Integer channelId, Integer status, Long beginTime, Long endTime);
	
	void save(DepositRecord depositRecord);

	DepositRecord findBySn(Long userId, String sn);

	DepositRecord findById(Long userId, Long id);

	@Deprecated
	public List<DepositRecord> findPending(Page page);
	
	@Deprecated
	List<DepositRecord> findPaySuccess(Page page);

}
