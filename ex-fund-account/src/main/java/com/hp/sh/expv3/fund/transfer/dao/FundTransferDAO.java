
package com.hp.sh.expv3.fund.transfer.dao;

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

	public List<FundTransfer> queryPending(Page page, @Param("notStatus1") Integer notStatus1, @Param("notStatus2") Integer notStatus2);

	public FundTransfer findByTradeNo(@Param("userId") Long userId, @Param("tradeNo") String tradeNo);

	public int changeStatus(
		@Param("userId") Long userId, 
		@Param("id") Long id,
		@Param("newStatus") Integer newStatus, 
		@Param("oldStatus") Integer oldStatus,
		@Param("errorInfo") String errorInfo,
		@Param("modified") Long modified
	);

}
