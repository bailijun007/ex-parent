/**
 * 
 */
package com.hp.sh.expv3.bb.module.collector.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.bb.error.BBCollectorAccountError;
import com.hp.sh.expv3.bb.module.collector.dao.BBCollectorAccountDAO;
import com.hp.sh.expv3.bb.module.collector.dao.BBCollectorAccountRecordDAO;
import com.hp.sh.expv3.bb.module.collector.entity.BBCollectorAccount;
import com.hp.sh.expv3.bb.module.collector.entity.BBCollectorAccountRecord;
import com.hp.sh.expv3.bb.vo.request.CollectorAddRequest;
import com.hp.sh.expv3.bb.vo.request.CollectorCutRequest;
import com.hp.sh.expv3.bb.vo.request.CollectorFundRequest;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.config.db.SnGenerator;
import com.hp.sh.expv3.constant.FundFlowDirection;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.error.ExCommonError;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.SnUtils;

/**
 * @author wangjg
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class BBCollectorCoreService{
	private static final Logger logger = LoggerFactory.getLogger(BBCollectorCoreService.class);

	@Autowired
	private BBCollectorAccountDAO collectorAccountDAO;

	@Autowired
	private BBCollectorAccountRecordDAO fundAccountRecordDAO;
	
	@Autowired
	private SnGenerator generator;
	/**
	 * 加钱
	 */
	public Integer add(@RequestBody CollectorAddRequest request){
		BBCollectorAccountRecord record = this.req2record(request);
		
		record.setType(FundFlowDirection.INCOME);
		record.setSn(generator.genSn(record));
		
		return this.newRecord(record);
	}
	
	/**
	 * 减钱
	 */
	public Integer cut(@RequestBody CollectorCutRequest request){
		BBCollectorAccountRecord record = this.req2record(request);
		
		record.setType(FundFlowDirection.EXPENSES);
		record.setSn(SnUtils.newRecordSn());
		
		return this.newRecord(record);
	}
	
	protected int newRecord(BBCollectorAccountRecord record){
		Long now = DbDateUtils.now();
		
		//金额必须是正数
		if(record.getAmount().compareTo(BigDecimal.ZERO)<0){
			throw new ExSysException(BBCollectorAccountError.ZERO_AMOUNT);
		}
		
		if(this.checkExist(record)){
			logger.warn("重复的请求！");
			return InvokeResult.NOCHANGE;
		}
		
		BBCollectorAccount collectorAccount = this.collectorAccountDAO.get(record.getCollectorId(), record.getAsset());
		BigDecimal recordAmount = record.getAmount().multiply(new BigDecimal(record.getType()));
		if(collectorAccount==null){
			//检查余额
			this.checkBalance(record, recordAmount);
			
			collectorAccount = this.newAccount(record.getCollectorId(), record.getAsset(), recordAmount, now);
		}else{
			BigDecimal newBalance = collectorAccount.getBalance().add(recordAmount);
			//检查余额
			this.checkBalance(record, newBalance);
			//更新余额
			collectorAccount.setModified(now);
			collectorAccount.setBalance(newBalance);
			this.updateAccount(collectorAccount);
		}
		
		//设置本比余额
		record.setSerialNo(collectorAccount.getVersion());
		record.setBalance(collectorAccount.getBalance());
		
		//保存记录
		record.setCreated(now);
		record.setModified(now);
		this.fundAccountRecordDAO.save(record);
		
		return InvokeResult.SUCCESS;
	}
	
	private void updateAccount(BBCollectorAccount bBCollectorAccount){
		int updatedRows = this.collectorAccountDAO.update(bBCollectorAccount);
		if(updatedRows==0){
			throw new UpdateException("更新失败", bBCollectorAccount);
		}
	}
	
	private void checkBalance(BBCollectorAccountRecord record, BigDecimal newBalance){
		//检查余额
		if(FundFlowDirection.EXPENSES==record.getType()){
			if(newBalance.compareTo(BigDecimal.ZERO) < 0){
				throw new ExException(BBCollectorAccountError.BALANCE_NOT_ENOUGH);
			}
		}
	}
	
	private BBCollectorAccount newAccount(Long collectorId, String asset, BigDecimal balance, Long now){
		BBCollectorAccount account = new BBCollectorAccount();
		account.setId(collectorId);
		account.setAsset(asset);
		account.setBalance(balance);
		account.setCreated(now);
		account.setModified(now);
		account.setVersion(0L);
		this.collectorAccountDAO.save(account);
		return account;
	}

	private boolean checkExist(BBCollectorAccountRecord record) {
		BBCollectorAccountRecord oldRcd = this.fundAccountRecordDAO.findByTradeNo(record.getCollectorId(), record.getTradeNo());
		if(oldRcd == null){
			return false;
		}
		
		if(oldRcd!=null){
			String ov = oldRcd.toValueString();
			String nv = record.toValueString();
			if(!ov.equals(nv)){
				throw new ExSysException(BBCollectorAccountError.INCONSISTENT_REQUESTS, ov, nv);
			}
		}
		
		return true;
	}

	private BBCollectorAccountRecord req2record(CollectorFundRequest request){
		if(request.getAmount()==null){
			throw new ExSysException(ExCommonError.PARAM_EMPTY);
		}
		BBCollectorAccountRecord record = new BBCollectorAccountRecord();
		record.setAmount(request.getAmount());
		record.setAsset(request.getAsset());
		record.setRemark(request.getRemark());
		record.setTradeNo(request.getTradeNo());
		record.setTradeType(request.getTradeType());
		record.setUserId(request.getUserId());
		record.setAssociatedId(request.getAssociatedId());
		record.setCollectorId(request.getCollectorId());
		return record;
	}
	
}

