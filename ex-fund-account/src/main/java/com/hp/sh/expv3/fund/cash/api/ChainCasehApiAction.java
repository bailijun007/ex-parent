package com.hp.sh.expv3.fund.cash.api;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.cash.component.Asset2Symbol;
import com.hp.sh.expv3.fund.cash.component.ExChainService;
import com.hp.sh.expv3.fund.cash.constant.PayChannel;
import com.hp.sh.expv3.fund.cash.service.complex.DepositService;
import com.hp.sh.expv3.fund.cash.service.complex.WithdrawalService;
import com.hp.sh.expv3.fund.wallet.api.FundAccountCoreApi;
import com.hp.sh.expv3.fund.wallet.constant.WalletError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 充值提现服务
 * @author wangjg
 *
 */
@Api(tags="BYS充值提现接口")
@RestController
public class ChainCasehApiAction implements ChainCasehApi{
	private static final Logger logger = LoggerFactory.getLogger(ChainCasehApiAction.class);
	
	@Autowired
	private ExChainService chainService;
	
	@Autowired
	private Asset2Symbol asset2Symbol;

	@Autowired
	private DepositService depositService;

	@Autowired
	private WithdrawalService withdrawalService;
	
	@Autowired
	private FundAccountCoreApi fundAccountCoreApi;
	
	int _____充值______;
	
	@ApiOperation(value = "1、获取BYS充值地址")
	public String getBysAddress(Long userId, String asset){
		Integer symbolId = asset2Symbol.getSymbol(asset);
		String address = chainService.getAddress(userId, symbolId);
		return address;
	}
	
	@ApiOperation(value = "2、验证BYS充值地址")
	public boolean verifyAddress(String asset, String address){
		Integer symbol = asset2Symbol.getSymbol(asset);
		return chainService.checkAddress(symbol);
	}

	//bys callback
	@ApiOperation(value = "3、创建Bys充值记录")
	public String createDeposit(Long userId, String chainOrderId, String asset, String account, BigDecimal amount, String txHash) {
		String sn = this.depositService.deposit(userId, asset, account, amount, chainOrderId, PayChannel.BYS, txHash);
		return sn;
	}
	
	//bys callback
	@ApiOperation(value = "4、充值成功")
	public void depositSuccess(Long userId, String sn){
		this.depositService.onPaySuccess(userId, sn);
	}
	
	@ApiOperation(value = "5、充值失败")
	public void depositFail(Long userId, String sn){
		this.depositService.onPayFail(userId, sn);
	}
	
	int _____提现______;
	
	@ApiOperation(value = "1、创建Bys提款记录")
	public void createDraw(Long userId, String asset, String address, BigDecimal amount) {
		BigDecimal balance = fundAccountCoreApi.getBalance(userId, asset);
		if(balance==null || balance.compareTo(amount)<0){
			throw new ExException(WalletError.NOT_ENOUGH);
		}
		this.withdrawalService.createWithdrawal(userId, asset, address, amount, null, PayChannel.BYS);
	}
	
	@ApiOperation(value = "2、批准提现")
	public void approve(Long userId, Long id){
		//修改状态
		withdrawalService.approveWithdrawal(userId, id);
	}
	
	@ApiOperation(value = "3、拒绝提现")
	public void reject(Long userId, Long id, String remark){
		withdrawalService.rejectWithdrawal(userId, id);
	}

}
