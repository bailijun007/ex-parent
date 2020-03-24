/**
 * 
 */
package com.hp.sh.expv3.bb.module.account.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.bb.error.BBAccountError;
import com.hp.sh.expv3.bb.module.account.dao.BBAccountDAO;
import com.hp.sh.expv3.bb.module.account.dao.BBAccountRecordDAO;
import com.hp.sh.expv3.bb.module.account.entity.BBAccount;
import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecord;
import com.hp.sh.expv3.bb.vo.request.FundRequest;
import com.hp.sh.expv3.bb.vo.request.BBAddRequest;
import com.hp.sh.expv3.bb.vo.request.BBCutRequest;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
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
public class BBAccountCoreService{
	private static final Logger logger = LoggerFactory.getLogger(BBAccountCoreService.class);

	@Autowired
	private BBAccountDAO bBAccountDAO;

	@Autowired
	private BBAccountRecordDAO fundAccountRecordDAO;
    
    @Autowired
    private ApplicationEventPublisher publisher;
	
	public Boolean exist(Long userId, String asset){
		BBAccount fa = this.bBAccountDAO.get(userId, asset);
		return fa!=null;
	}

	public int createAccount(Long userId, String asset){
		BBAccount fa = this.bBAccountDAO.get(userId, asset);
		if(fa!=null){
			return InvokeResult.NOCHANGE;
		}
		this.newPcAccount(userId, asset, BigDecimal.ZERO, DbDateUtils.now());
		
		return InvokeResult.SUCCESS;
	}
	
	public BigDecimal getBalance(Long userId, String asset){
		BBAccount fa = this.bBAccountDAO.get(userId, asset);
		if(fa==null){
			return null;
		}
		return fa.getBalance();
	}

	/**
	 * 加钱
	 */
	public Integer add(@RequestBody BBAddRequest request){
		BBAccountRecord record = this.req2record(request);
		
		record.setType(FundFlowDirection.INCOME);
		record.setSn(SnUtils.newRecordSn());
		
		return this.newRecord(record);
	}
	
	/**
	 * 减钱
	 */
	public Integer cut(@RequestBody BBCutRequest request){
		BBAccountRecord record = this.req2record(request);
		
		record.setType(FundFlowDirection.EXPENSES);
		record.setSn(SnUtils.newRecordSn());
		
		return this.newRecord(record);
	}
	
	public Boolean checkTradNo(Long userId, String tradeNo) {
		BBAccountRecord rcd = this.fundAccountRecordDAO.findByTradeNo(userId, tradeNo);
		if (rcd == null) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	BBAccountRecord queryRecord(Long userId, String tradeNo){
		BBAccountRecord record = this.fundAccountRecordDAO.findByTradeNo(userId, tradeNo);
		if(record==null){
			throw new ExSysException(ExCommonError.OBJ_DONT_EXIST);
		}
		return record;
	}

	protected int newRecord(BBAccountRecord record){
		Long now = DbDateUtils.now();
		
		//金额必须是正数
		if(record.getAmount().compareTo(BigDecimal.ZERO)<0){
			throw new ExSysException(ExCommonError.PARAM_EMPTY);
		}
		
		if(this.checkExist(record)){
			logger.warn("重复的请求！");
			return InvokeResult.NOCHANGE;
		}
		
		BBAccount bBAccount = this.bBAccountDAO.getAndLock(record.getUserId(), record.getAsset());
		BigDecimal recordAmount = record.getAmount().multiply(new BigDecimal(record.getType()));
		if(bBAccount==null){
			//检查余额
			this.checkBalance(record, recordAmount);
			
			bBAccount = this.newPcAccount(record.getUserId(), record.getAsset(), recordAmount, now);
		}else{
			BigDecimal newBalance = bBAccount.getBalance().add(recordAmount);
			//检查余额
			this.checkBalance(record, newBalance);
			//更新余额
			bBAccount.setModified(now);
			bBAccount.setBalance(newBalance);
			this.updateAccount(bBAccount);
		}
		
		//设置本比余额
		record.setSerialNo(bBAccount.getVersion());
		record.setBalance(bBAccount.getBalance());
		
		//保存记录
		record.setCreated(now);
		record.setModified(now);
		this.fundAccountRecordDAO.save(record);
		
		publisher.publishEvent(record);
	
		return InvokeResult.SUCCESS;
	}
	
	private void updateAccount(BBAccount bBAccount){
		int updatedRows = this.bBAccountDAO.update(bBAccount);
		if(updatedRows==0){
			throw new UpdateException("更新失败");
		}
	}
	
	private void checkBalance(BBAccountRecord record, BigDecimal newBalance){
		//检查余额
		if(FundFlowDirection.EXPENSES==record.getType()){
			if(newBalance.compareTo(BigDecimal.ZERO) < 0){
				throw new ExException(BBAccountError.BALANCE_NOT_ENOUGH, record.getUserId(), record, newBalance);
			}
		}
	}
	
	private BBAccount newPcAccount(Long userId, String asset, BigDecimal balance, Long now){
		BBAccount account = new BBAccount();
		account.setAsset(asset);
		account.setBalance(balance);
		account.setUserId(userId);
		account.setCreated(now);
		account.setModified(now);
		account.setVersion(0L);
		this.bBAccountDAO.save(account);
		return account;
	}

	private boolean checkExist(BBAccountRecord record) {
		BBAccountRecord oldRcd = this.fundAccountRecordDAO.findByTradeNo(record.getUserId(), record.getTradeNo());
		if(oldRcd == null){
			return false;
		}
		
		if(oldRcd!=null){
			String ov = oldRcd.toValueString();
			String nv = record.toValueString();
			if(!ov.equals(nv)){
				throw new ExSysException(BBAccountError.INCONSISTENT_REQUESTS, ov, nv);
			}
		}
		
		return true;
	}

	private BBAccountRecord req2record(FundRequest request){
		if(request.getAmount()==null){
			throw new ExSysException(ExCommonError.PARAM_EMPTY);
		}
		BBAccountRecord record = new BBAccountRecord();
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

