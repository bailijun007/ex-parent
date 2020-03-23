/**
 * 
 */
package com.hp.sh.expv3.pc.module.riskfund.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.constant.FundFlowDirection;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.error.ExCommonError;
import com.hp.sh.expv3.pc.error.PcRiskfundAccountError;
import com.hp.sh.expv3.pc.module.riskfund.dao.PcRiskfundAccountDAO;
import com.hp.sh.expv3.pc.module.riskfund.dao.PcRiskfundAccountRecordDAO;
import com.hp.sh.expv3.pc.module.riskfund.entity.PcRiskfundAccount;
import com.hp.sh.expv3.pc.module.riskfund.entity.PcRiskfundAccountRecord;
import com.hp.sh.expv3.pc.vo.request.CollectorFundRequest;
import com.hp.sh.expv3.pc.vo.request.RiskFundRequest;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.SnUtils;

/**
 * @author wangjg
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcRiskfundCoreService{
	private static final Logger logger = LoggerFactory.getLogger(PcRiskfundCoreService.class);

	@Autowired
	private PcRiskfundAccountDAO accountDAO;

	@Autowired
	private PcRiskfundAccountRecordDAO accountRecordDAO;
	
	/**
	 * 加钱
	 */
	public Integer add(RiskFundRequest request){
		PcRiskfundAccountRecord record = this.convertRecord(request);
		
		record.setType(FundFlowDirection.INCOME);
		record.setSn(SnUtils.newRecordSn());
		
		return this.newRecord(record);
	}
	
	/**
	 * 减钱
	 */
	public Integer cut(RiskFundRequest request){
		PcRiskfundAccountRecord record = this.convertRecord(request);
		
		record.setType(FundFlowDirection.EXPENSES);
		record.setSn(SnUtils.newRecordSn());
		
		return this.newRecord(record);
	}
	
	protected int newRecord(PcRiskfundAccountRecord record){
		Long now = DbDateUtils.now();
		
		//金额必须是正数
		if(record.getAmount().compareTo(BigDecimal.ZERO)<0){
			throw new ExSysException(ExCommonError.PARAM_EMPTY);
		}
		
		if(this.checkExist(record)){
			logger.warn("重复的请求！");
			return InvokeResult.NOCHANGE;
		}
		
		PcRiskfundAccount account = this.accountDAO.get(record.getAsset());
		BigDecimal recordAmount = record.getAmount().multiply(new BigDecimal(record.getType()));
		if(account==null){
			//检查余额
			this.checkBalance(record, recordAmount);
			
			account = this.newAccount(record.getRiskfundAccountId(), record.getAsset(), recordAmount, now);
		}else{
			BigDecimal newBalance = account.getBalance().add(recordAmount);
			//检查余额
			this.checkBalance(record, newBalance);
			//更新余额
			account.setModified(now);
			account.setBalance(newBalance);
			this.updateAccount(account);
		}
		
		//设置本比余额
		record.setSerialNo(account.getVersion());
		record.setBalance(account.getBalance());
		
		//保存记录
		record.setCreated(now);
		record.setModified(now);
		record.setRiskfundAccountId(account.getId());
		
		this.accountRecordDAO.save(record);
		
		return InvokeResult.SUCCESS;
	}
	
	private void updateAccount(PcRiskfundAccount bBCollectorAccount){
		int updatedRows = this.accountDAO.update(bBCollectorAccount);
		if(updatedRows==0){
			throw new RuntimeException("更新失败");
		}
	}
	
	private void checkBalance(PcRiskfundAccountRecord record, BigDecimal newBalance){
		//检查余额
		if(FundFlowDirection.EXPENSES==record.getType()){
			if(newBalance.compareTo(BigDecimal.ZERO) < 0){
				throw new ExException(PcRiskfundAccountError.BALANCE_NOT_ENOUGH);
			}
		}
	}
	
	private PcRiskfundAccount newAccount(Long collectorId, String asset, BigDecimal balance, Long now){
		PcRiskfundAccount account = new PcRiskfundAccount();
		account.setAsset(asset);
		account.setBalance(balance);
		account.setId(collectorId);
		account.setCreated(now);
		account.setModified(now);
		account.setVersion(0L);
		this.accountDAO.save(account);
		return account;
	}

	private boolean checkExist(PcRiskfundAccountRecord record) {
		PcRiskfundAccountRecord oldRcd = this.accountRecordDAO.findByTradeNo(record.getRiskfundAccountId(), record.getTradeNo());
		if(oldRcd == null){
			return false;
		}
		
		if(oldRcd!=null){
			String ov = oldRcd.toValueString();
			String nv = record.toValueString();
			if(!ov.equals(nv)){
				throw new ExSysException(PcRiskfundAccountError.INCONSISTENT_REQUESTS, ov, nv);
			}
		}
		
		return true;
	}

	private PcRiskfundAccountRecord convertRecord(RiskFundRequest request){
		if(request.getAmount()==null){
			throw new ExSysException(ExCommonError.PARAM_EMPTY);
		}
		PcRiskfundAccountRecord record = new PcRiskfundAccountRecord();
		record.setAmount(request.getAmount());
		record.setAsset(request.getAsset());
		record.setRemark(request.getRemark());
		record.setTradeNo(request.getTradeNo());
		record.setTradeType(request.getTradeType());
		return record;
	}
	
}

