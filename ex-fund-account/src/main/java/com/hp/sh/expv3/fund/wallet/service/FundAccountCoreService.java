/**
 * 
 */
package com.hp.sh.expv3.fund.wallet.service;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.fund.wallet.constant.FundFlowDirection;
import com.hp.sh.expv3.fund.wallet.constant.WalletError;
import com.hp.sh.expv3.fund.wallet.dao.FundAccountDAO;
import com.hp.sh.expv3.fund.wallet.dao.FundAccountRecordDAO;
import com.hp.sh.expv3.fund.wallet.entity.FundAccount;
import com.hp.sh.expv3.fund.wallet.entity.FundAccountRecord;
import com.hp.sh.expv3.fund.wallet.vo.request.AddMoneyRequest;
import com.hp.sh.expv3.fund.wallet.vo.request.CutMoneyRequest;
import com.hp.sh.expv3.fund.wallet.vo.request.FundRequest;
import com.hp.sh.expv3.utils.SnUtils;

/**
 * @author wangjg
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class FundAccountCoreService{
	private static final Logger logger = LoggerFactory.getLogger(FundAccountCoreService.class);

	@Autowired
	private FundAccountDAO fundAccountDAO;

	@Autowired
	private FundAccountRecordDAO fundAccountRecordDAO;

	public int createAccount(Long userId, String asset){
		FundAccount fa = this.fundAccountDAO.get(userId, asset);
		if(fa!=null){
			return InvokeResult.NOCHANGE;
		}
		this.createFundAccount(userId, asset, BigDecimal.ZERO, new Date());
		
		return InvokeResult.SUCCESS;
	}
	
	public BigDecimal getBalance(Long userId, String asset){
		FundAccount fa = this.fundAccountDAO.get(userId, asset);
		if(fa==null){
			return null;
		}
		return fa.getBalance();
	}

	/**
	 * 加钱
	 */
	public void add(@RequestBody AddMoneyRequest request){
		FundAccountRecord record = this.req2record(request);
		
		record.setType(FundFlowDirection.INCOME);
		record.setSn(SnUtils.genRecordSn());
		
		this.newRecord(record);
	}
	
	/**
	 * 减钱
	 */
	public void cut(@RequestBody CutMoneyRequest request){
		FundAccountRecord record = this.req2record(request);
		
		record.setType(FundFlowDirection.EXPENSES);
		record.setSn(SnUtils.genRecordSn());
		
		this.newRecord(record);
	}
	
	FundAccountRecord queryRecord(Long userId, String tradeNo){
		FundAccountRecord record = this.fundAccountRecordDAO.findByTradeNo(userId, tradeNo);
		if(record==null){
			throw new ExException(CommonError.OBJ_DONT_EXIST);
		}
		return record;
	}

	public Boolean checkTradNo(Long userId, String tradeNo) {
		FundAccountRecord rcd = this.fundAccountRecordDAO.findByTradeNo(userId, tradeNo);
		if (rcd == null) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	protected int newRecord(FundAccountRecord record){
		Date now = new Date();
		
		//金额必须是正数
		if(record.getAmount().compareTo(BigDecimal.ZERO)<0){
			throw new ExException(CommonError.PARAM_ERROR);
		}
		
		if(this.checkExist(record)){
			logger.warn("重复的请求！");
			return InvokeResult.NOCHANGE;
		}
		
		FundAccount fundAccount = this.fundAccountDAO.getAndLock(record.getUserId(), record.getAsset());
		BigDecimal recordAmount = record.getAmount().multiply(new BigDecimal(record.getType()));
		if(fundAccount==null){
			fundAccount = this.createFundAccount(record.getUserId(), record.getAsset(), recordAmount, now);
		}else{
			BigDecimal newBalance = fundAccount.getBalance().add(recordAmount);
			//检查余额
			this.checkBalance(record, newBalance);
			
			fundAccount.setModified(now);
			fundAccount.setBalance(newBalance);
			this.updateAccount(fundAccount);
		}
		
		//balance
		record.setSerialNo(fundAccount.getVersion());
		record.setBalance(fundAccount.getBalance());
		
		//date
		record.setCreated(now);
		record.setModified(now);
		this.fundAccountRecordDAO.save(record);
	
		return InvokeResult.SUCCESS;
	}
	
	private void updateAccount(FundAccount fundAccount){
		int updatedRows = this.fundAccountDAO.update(fundAccount);
		if(updatedRows==0){
			throw new RuntimeException("更新失败");
		}
	}
	
	private void checkBalance(FundAccountRecord record, BigDecimal newBalance){
		//检查余额
		if(FundFlowDirection.EXPENSES==record.getType()){
			if(newBalance.compareTo(BigDecimal.ZERO) < 0){
				throw new ExException(WalletError.NOT_ENOUGH);
			}
		}
	}
	
	private FundAccount createFundAccount(Long userId, String asset, BigDecimal balance, Date now){
		FundAccount account = new FundAccount();
		account.setAsset(asset);
		account.setBalance(balance);
		account.setUserId(userId);
		account.setCreated(now);
		account.setModified(now);
		account.setVersion(0L);
		this.fundAccountDAO.save(account);
		return account;
	}

	private boolean checkExist(FundAccountRecord record) {
		FundAccountRecord oldRcd = this.fundAccountRecordDAO.findByTradeNo(record.getUserId(), record.getTradeNo());
		if(oldRcd == null){
			return false;
		}
		
		if(oldRcd!=null){
			String ov = oldRcd.toValueString();
			String nv = record.toValueString();
			if(!ov.equals(nv)){
				throw new ExException(WalletError.INCONSISTENT_REQUESTS);
			}
		}
		
		return true;
	}

	private FundAccountRecord req2record(FundRequest request){
		FundAccountRecord record = new FundAccountRecord();
		record.setAmount(request.getAmount());
		record.setAsset(request.getAsset());
		record.setRemark(request.getRemark());
		record.setTradeNo(request.getTradeNo());
		record.setTradeType(request.getTradeType());
		record.setUserId(request.getUserId());
		return record;
	}
	
}
