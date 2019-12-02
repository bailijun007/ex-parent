package com.hp.sh.expv3.fund.api4v2;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.base.BaseApiAction;
import com.hp.sh.expv3.fund.cash.api.ChainCasehApiAction;
import com.hp.sh.expv3.fund.cash.entity.WithdrawalAddr;
import com.hp.sh.expv3.fund.cash.service.DepositAddrService;
import com.hp.sh.expv3.fund.cash.service.WithdrawAddrService;
import com.hp.sh.expv3.fund.wallet.api.FundAccountCoreApi;

@RestController
@RequestMapping("/inner/api/msg/send3")
public class FundAccount4Server extends BaseApiAction implements FundAccount4ServerDef{
	
	@Autowired
	private ChainCasehApiAction chainCasehApiAction;
	
	@Autowired
	private FundAccountCoreApi fundAccountCoreApi;
	
	@Autowired
	private WithdrawAddrService withdrawAddrService;
	
	@Autowired
	private DepositAddrService depositAddrService;

	public boolean createFundAccount(String operator, long accountId, String asset, String token) {
		this.fundAccountCoreApi.createAccount(accountId, asset);
		return true;
	}
	
	@Override
	public boolean transfer2PcAccount(String operator, long accountId, String asset, BigDecimal volume, String token) {
		// TODO 不实现，转到永续合约
		return false;
	}

	@Override
	public String getDepositAddress(String operator, long accountId, String asset, String token) {
		String addr = chainCasehApiAction.getBysAddress(accountId, asset);
		return addr;
	}

	@Override
	public long saveWithdrawAddress(String operator, long accountId, String asset, String address, String remark, String token) {
		WithdrawalAddr withdrawalAddr = new WithdrawalAddr();
		withdrawalAddr.setAddress(address);
		withdrawalAddr.setAsset(asset);
		withdrawalAddr.setEnabled(WithdrawalAddr.YES);
		withdrawalAddr.setRemark(remark);
		withdrawAddrService.save(withdrawalAddr);
		return withdrawalAddr.getId();
	}

	@Override
	public boolean updateWithdrawAddressRemark(String operator, long accountId, String asset, long withdrawAddrId, String remark, String token) {
		WithdrawalAddr withdrawalAddr = withdrawAddrService.findWithdrawAddr(accountId, asset);
		withdrawalAddr.setRemark(remark);
		this.withdrawAddrService.update(withdrawalAddr);
		return true;
	}

	@Override
	public boolean deleteWithdrawAddress(String operator, long accountId, String asset, long withdrawAddrId, String token) {
		WithdrawalAddr withdrawalAddr = withdrawAddrService.findWithdrawAddr(accountId, asset);
		this.withdrawAddrService.delete(accountId, withdrawalAddr.getId());
		return true;
	}

	@Override
	public long withdraw(String operator, long accountId, String asset, String withdrawAddr, BigDecimal volume, String token) {
		this.chainCasehApiAction.createDraw(accountId, asset, withdrawAddr, volume);
		return -1;
	}

	@Override
	public boolean verifyAddress(String operator, String asset, String address, String token) {
		return chainCasehApiAction.verifyAddress(asset, address);
	}

	@Override
	public boolean addLeafFeeRebate(String operator, long accountId, String asset, BigDecimal delta, String token) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
