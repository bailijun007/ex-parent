/**
 * 
 */
package com.hp.sh.expv3.pc.module.account.service.impl;

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
import com.hp.sh.expv3.constant.FundFlowDirection;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.pc.module.account.api.PcAccountCoreApi;
import com.hp.sh.expv3.pc.module.account.api.request.AddMoneyRequest;
import com.hp.sh.expv3.pc.module.account.api.request.CutMoneyRequest;
import com.hp.sh.expv3.pc.module.account.constant.AccountError;
import com.hp.sh.expv3.pc.module.account.dao.PcAccountDAO;
import com.hp.sh.expv3.pc.module.account.dao.PcAccountRecordDAO;
import com.hp.sh.expv3.pc.module.account.entity.PcAccount;
import com.hp.sh.expv3.pc.module.account.entity.PcAccountRecord;
import com.hp.sh.expv3.utils.SnUtils;

/**
 * @author wangjg
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcAccountCoreService implements PcAccountCoreApi {
	private static final Logger logger = LoggerFactory.getLogger(PcAccountCoreService.class);

	@Autowired
	private PcAccountDAO fundAccountDAO;

	@Autowired
	private PcAccountRecordDAO fundAccountRecordDAO;

	@Override
	public int createAccount(Long userId, String asset){
		PcAccount fa = this.fundAccountDAO.get(userId, asset);
		if(fa!=null){
			return InvokeResult.NOCHANGE;
		}
		this.newPcAccount(userId, asset, BigDecimal.ZERO, new Date());
		
		return InvokeResult.SUCCESS;
	}
	
	@Override
	public BigDecimal getBalance(Long userId, String asset){
		PcAccount fa = this.fundAccountDAO.get(userId, asset);
		if(fa==null){
			return null;
		}
		return fa.getBalance();
	}

	/**
	 * 加钱
	 */
	@Override
	public Integer add(@RequestBody AddMoneyRequest request){
		PcAccountRecord record = this.req2record(request);
		
		record.setType(FundFlowDirection.INCOME);
		record.setSn(SnUtils.genRecordSn());
		
		return this.newRecord(record);
	}
	
	/**
	 * 减钱
	 */
	@Override
	public Integer cut(@RequestBody CutMoneyRequest request){
		PcAccountRecord record = this.req2record(request);
		
		record.setType(FundFlowDirection.EXPENSES);
		record.setSn(SnUtils.genRecordSn());
		
		return this.newRecord(record);
	}
	
	@Override
	public Boolean checkTradNo(Long userId, String tradeNo) {
		PcAccountRecord rcd = this.fundAccountRecordDAO.findByTradeNo(userId, tradeNo);
		if (rcd == null) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	PcAccountRecord queryRecord(Long userId, String tradeNo){
		PcAccountRecord record = this.fundAccountRecordDAO.findByTradeNo(userId, tradeNo);
		if(record==null){
			throw new ExException(CommonError.OBJ_DONT_EXIST);
		}
		return record;
	}

	protected int newRecord(PcAccountRecord record){
		Date now = new Date();
		
		//金额必须是正数
		if(record.getAmount().compareTo(BigDecimal.ZERO)<0){
			throw new ExException(CommonError.PARAM_ERROR);
		}
		
		if(this.checkExist(record)){
			logger.warn("重复的请求！");
			return InvokeResult.NOCHANGE;
		}
		
		PcAccount pcAccount = this.fundAccountDAO.getAndLock(record.getUserId(), record.getAsset());
		BigDecimal recordAmount = record.getAmount().multiply(new BigDecimal(record.getType()));
		if(pcAccount==null){
			pcAccount = this.newPcAccount(record.getUserId(), record.getAsset(), recordAmount, now);
		}else{
			BigDecimal newBalance = pcAccount.getBalance().add(recordAmount);
			//检查余额
			this.checkBalance(record, newBalance);
			//更新余额
			pcAccount.setModified(now);
			pcAccount.setBalance(newBalance);
			this.fundAccountDAO.update(pcAccount);
		}
		
		//设置本比余额
		record.setSerialNo(pcAccount.getVersion());
		record.setBalance(pcAccount.getBalance());
		
		//保存记录
		record.setCreated(now);
		record.setModified(now);
		this.fundAccountRecordDAO.save(record);
	
		return InvokeResult.SUCCESS;
	}
	
	private void checkBalance(PcAccountRecord record, BigDecimal newBalance){
		//检查余额
		if(FundFlowDirection.EXPENSES==record.getType()){
			if(newBalance.compareTo(BigDecimal.ZERO) < 0){
				throw new ExException(AccountError.BALANCE_NOT_ENOUGH);
			}
		}
	}
	
	private PcAccount newPcAccount(Long userId, String asset, BigDecimal balance, Date now){
		PcAccount account = new PcAccount();
		account.setAsset(asset);
		account.setBalance(balance);
		account.setUserId(userId);
		account.setCreated(now);
		account.setModified(now);
		account.setVersion(0L);
		this.fundAccountDAO.save(account);
		return account;
	}

	private boolean checkExist(PcAccountRecord record) {
		PcAccountRecord oldRcd = this.fundAccountRecordDAO.findByTradeNo(record.getUserId(), record.getTradeNo());
		if(oldRcd == null){
			return false;
		}
		
		if(oldRcd!=null){
			String ov = oldRcd.toValueString();
			String nv = record.toValueString();
			if(!ov.equals(nv)){
				throw new ExException(AccountError.INCONSISTENT_REQUESTS);
			}
		}
		
		return true;
	}

	private PcAccountRecord req2record(AddMoneyRequest request){
		PcAccountRecord record = new PcAccountRecord();
		record.setAmount(request.getAmount());
		record.setAsset(request.getAsset());
		record.setRemark(request.getRemark());
		record.setTradeNo(request.getTradeNo());
		record.setTradeType(request.getTradeType());
		record.setUserId(request.getUserId());
		record.setAssociatedId(request.getAssociatedId());
		return record;
	}
	
}

