/**
 * 
 */
package com.hp.sh.expv3.pc.module.collector.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.constant.FundFlowDirection;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.error.ExCommonError;
import com.hp.sh.expv3.pc.error.PcCollectorAccountError;
import com.hp.sh.expv3.pc.module.collector.dao.PcCollectorAccountDAO;
import com.hp.sh.expv3.pc.module.collector.dao.PcCollectorAccountRecordDAO;
import com.hp.sh.expv3.pc.module.collector.entity.PcCollectorAccount;
import com.hp.sh.expv3.pc.module.collector.entity.PcCollectorAccountRecord;
import com.hp.sh.expv3.pc.vo.request.CollectorAddRequest;
import com.hp.sh.expv3.pc.vo.request.CollectorCutRequest;
import com.hp.sh.expv3.pc.vo.request.CollectorFundRequest;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.SnUtils;

/**
 * @author wangjg
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcCollectorCoreService{
	private static final Logger logger = LoggerFactory.getLogger(PcCollectorCoreService.class);

	@Autowired
	private PcCollectorAccountDAO collectorAccountDAO;

	@Autowired
	private PcCollectorAccountRecordDAO fundAccountRecordDAO;
	
	/**
	 * 加钱
	 */
	public Integer add(@RequestBody CollectorAddRequest request){
		PcCollectorAccountRecord record = this.req2record(request);
		
		record.setType(FundFlowDirection.INCOME);
		record.setSn(SnUtils.newRecordSn());
		
		return this.newRecord(record);
	}
	
	/**
	 * 减钱
	 */
	public Integer cut(@RequestBody CollectorCutRequest request){
		PcCollectorAccountRecord record = this.req2record(request);
		
		record.setType(FundFlowDirection.EXPENSES);
		record.setSn(SnUtils.newRecordSn());
		
		return this.newRecord(record);
	}
	
	protected int newRecord(PcCollectorAccountRecord record){
		Long now = DbDateUtils.now();
		
		//金额必须是正数
		if(record.getAmount().compareTo(BigDecimal.ZERO)<0){
			throw new ExSysException(ExCommonError.REQUIRE_POSITIVE);
		}
		
		if(this.checkExist(record)){
			logger.warn("重复的请求！");
			return InvokeResult.NOCHANGE;
		}
		
		PcCollectorAccount collectorAccount = this.collectorAccountDAO.get(record.getCollectorId(), record.getAsset());
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
	
	private void updateAccount(PcCollectorAccount bBCollectorAccount){
		int updatedRows = this.collectorAccountDAO.update(bBCollectorAccount);
		if(updatedRows==0){
			throw new UpdateException("更新失败");
		}
	}
	
	private void checkBalance(PcCollectorAccountRecord record, BigDecimal newBalance){
		//检查余额
		if(newBalance.compareTo(BigDecimal.ZERO) < 0){
			throw new ExException(PcCollectorAccountError.BALANCE_NOT_ENOUGH);
		}
	}
	
	private PcCollectorAccount newAccount(Long collectorId, String asset, BigDecimal balance, Long now){
		PcCollectorAccount account = new PcCollectorAccount();
		account.setAsset(asset);
		account.setBalance(balance);
		account.setId(collectorId);
		account.setCreated(now);
		account.setModified(now);
		account.setVersion(0L);
		this.collectorAccountDAO.save(account);
		return account;
	}

	private boolean checkExist(PcCollectorAccountRecord record) {
		PcCollectorAccountRecord oldRcd = this.fundAccountRecordDAO.findByTradeNo(record.getCollectorId(), record.getTradeNo());
		if(oldRcd == null){
			return false;
		}
		
		if(oldRcd!=null){
			String ov = oldRcd.toValueString();
			String nv = record.toValueString();
			if(!ov.equals(nv)){
				throw new ExSysException(PcCollectorAccountError.INCONSISTENT_REQUESTS, ov, nv);
			}
		}
		
		return true;
	}

	private PcCollectorAccountRecord req2record(CollectorFundRequest request){
		if(request.getAmount()==null){
			throw new ExSysException(ExCommonError.PARAM_EMPTY);
		}
		PcCollectorAccountRecord record = new PcCollectorAccountRecord();
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

