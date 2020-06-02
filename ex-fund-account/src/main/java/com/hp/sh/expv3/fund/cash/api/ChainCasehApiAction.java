package com.hp.sh.expv3.fund.cash.api;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.fund.cash.component.Asset2Symbol;
import com.hp.sh.expv3.fund.cash.component.ExChainService;
import com.hp.sh.expv3.fund.cash.component.MetadataService;
import com.hp.sh.expv3.fund.cash.entity.DepositAddr;
import com.hp.sh.expv3.fund.cash.entity.WithdrawalRecord;
import com.hp.sh.expv3.fund.cash.mq.WithDrawalMsg;
import com.hp.sh.expv3.fund.cash.mq.WithDrawalSender;
import com.hp.sh.expv3.fund.cash.service.DepositAddrService;
import com.hp.sh.expv3.fund.cash.service.complex.DepositService;
import com.hp.sh.expv3.fund.cash.service.complex.WithdrawalService;
import com.hp.sh.expv3.fund.constant.PayChannel;
import com.hp.sh.expv3.fund.wallet.api.FundAccountCoreApi;
import com.hp.sh.expv3.fund.wallet.error.WalletError;
import com.hp.sh.expv3.utils.CheckUtils;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.BigUtils;

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
	private DepositAddrService depositAddrService;
	
	@Autowired
	private FundAccountCoreApi fundAccountCoreApi;
	
	@Autowired
	private WithDrawalSender mqSender;
	
	@Autowired
	private MetadataService metadataService;
	
	int _____充值______;
	
	@ApiOperation(value = "1、获取充币地址")
	@LockIt(key="dpositAddress-${userId}-${asset}")
	public String getDepositAddress(Long userId, String asset){
		DepositAddr addr = depositAddrService.getDepositAddress(userId, asset);
		if(addr==null){
			Integer symbolId = asset2Symbol.getSymbol(asset);
			String address = chainService.getAddress(userId, symbolId);
	        addr = new DepositAddr();
	        addr.setAsset(asset);
	        addr.setUserId(userId);
	        addr.setAddress(address);
	        addr.setRemark("bys:"+symbolId);
	        addr.setEnabled(IntBool.YES);
	        this.depositAddrService.save(addr);
		}
        return addr.getAddress();
	}
	
	@Override
	@ApiOperation(value = "2、验证地址")
	public boolean verifyAddress(String asset, String address){
		Integer symbol = asset2Symbol.getSymbol(asset);
		return chainService.checkAddress(symbol);
	}

	//bys callback
	@ApiOperation(value = "3、创建充值记录")
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
	
	@ApiOperation(value = "1、创建提款记录")
	public void createWithdrawal(Long userId, String asset, String address, BigDecimal amount) {
		CheckUtils.checkRequired(userId, asset, address, amount);
		
		CheckUtils.checkPositiveNum(amount);
		
		BigDecimal balance = fundAccountCoreApi.getBalance(userId, asset);
		if(balance==null || balance.compareTo(amount)<0){
			throw new ExException(WalletError.NOT_ENOUGH);
		}

		BigDecimal withdrawFee = metadataService.getWithdrawFee(asset);
		if(BigUtils.le(balance.subtract(withdrawFee), amount)){
			throw new ExException(WalletError.NOT_ENOUGH);
		}
		
		this.withdrawalService.createWithdrawal(userId, asset, address, amount, withdrawFee, null, PayChannel.BYS);
	}
	
	@Override
	@ApiOperation(value = "2、批准提现")
	public void approve(Long userId, Long id){
		//修改状态
		WithdrawalRecord record = withdrawalService.approveWithdrawal(userId, id);
		
		//发消息
		if(record!=null){
			mqSender.send(new WithDrawalMsg(record.getUserId(), record.getId()));
		}
	}
	
	@Override
	@ApiOperation(value = "3、拒绝提现")
	public void reject(Long userId, Long id, String remark){
		withdrawalService.rejectWithdrawal(userId, id);
	}

}
