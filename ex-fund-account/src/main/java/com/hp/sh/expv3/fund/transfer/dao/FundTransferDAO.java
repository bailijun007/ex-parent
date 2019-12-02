
package com.hp.sh.expv3.fund.transfer.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.base.mapper.BaseAccountDataMapper;
import com.hp.sh.expv3.fund.transfer.entity.FundTransfer;

/**
 * 
 * @author wangjg
 *
 */
public interface FundTransferDAO extends BaseAccountDataMapper<FundTransfer, Long> {

	public List<FundTransfer> queryPending(Page page);

	public FundTransfer findByTradeNo(@Param("userId") Long userId, @Param("tradeNo") String tradeNo);

	public void changeStatus(
		@Param("userId") Long userId, 
		@Param("id") Long id,
		@Param("newStatus") Integer newStatus, 
		@Param("oldStatus") Integer oldStatus,
		@Param("modified") Date modified
	);

}
