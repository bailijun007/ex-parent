package com.hp.sh.expv3.fund.cash.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.fund.cash.entity.WithdrawalAddr;
import com.hp.sh.expv3.fund.cash.service.WithdrawAddrService;

/**
 * 提币地址接口
 * @author wangjg
 *
 */
@RestController
public class WithdrawalAddrApiAction implements WithdrawalAddrApi{
	private static final Logger logger = LoggerFactory.getLogger(WithdrawalAddrApiAction.class);
	
	@Autowired
	private WithdrawAddrService withdrawAddrService;

	@Override
	public long save(long userId, String asset, String address, String remark) {
		WithdrawalAddr withdrawalAddr = new WithdrawalAddr();
		withdrawalAddr.setUserId(userId);
		withdrawalAddr.setAddress(address);
		withdrawalAddr.setAsset(asset);
		withdrawalAddr.setEnabled(WithdrawalAddr.YES);
		withdrawalAddr.setRemark(remark);
		withdrawAddrService.save(withdrawalAddr);
		return withdrawalAddr.getId();
	}

	@Override
	public boolean updateRemark(long userId, String asset, long withdrawAddrId, String remark) {
		WithdrawalAddr withdrawalAddr = withdrawAddrService.findWithdrawAddr(userId, asset);
		withdrawalAddr.setRemark(remark);
		this.withdrawAddrService.update(withdrawalAddr);
		return true;
	}

	@Override
	public boolean delete(long userId, String asset, long withdrawAddrId) {
		this.withdrawAddrService.delete(userId, withdrawAddrId);
		return true;
	}

}
