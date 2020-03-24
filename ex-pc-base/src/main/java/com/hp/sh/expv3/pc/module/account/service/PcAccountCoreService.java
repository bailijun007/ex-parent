/**
 * 
 */
package com.hp.sh.expv3.pc.module.account.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.constant.FundFlowDirection;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.error.ExCommonError;
import com.hp.sh.expv3.pc.error.PcAccountError;
import com.hp.sh.expv3.pc.module.account.dao.PcAccountDAO;
import com.hp.sh.expv3.pc.module.account.dao.PcAccountRecordDAO;
import com.hp.sh.expv3.pc.module.account.entity.PcAccount;
import com.hp.sh.expv3.pc.module.account.entity.PcAccountRecord;
import com.hp.sh.expv3.pc.vo.request.FundRequest;
import com.hp.sh.expv3.pc.vo.request.PcAddRequest;
import com.hp.sh.expv3.pc.vo.request.PcCutRequest;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.SnUtils;
import com.hp.sh.expv3.utils.math.BigUtils;

/**
 * @author wangjg
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcAccountCoreService{
	private static final Logger logger = LoggerFactory.getLogger(PcAccountCoreService.class);

	@Autowired
	private PcAccountDAO pcAccountDAO;

	@Autowired
	private PcAccountRecordDAO fundAccountRecordDAO;
    
    @Autowired
    private ApplicationEventPublisher publisher;
	
	public Boolean exist(Long userId, String asset){
		PcAccount fa = this.pcAccountDAO.get(userId, asset);
		return fa!=null;
	}

	public int createAccount(Long userId, String asset){
		PcAccount fa = this.pcAccountDAO.get(userId, asset);
		if(fa!=null){
			return InvokeResult.NOCHANGE;
		}
		this.newPcAccount(userId, asset, BigDecimal.ZERO, DbDateUtils.now());
		
		return InvokeResult.SUCCESS;
	}
	
	public BigDecimal getBalance(Long userId, String asset){
		PcAccount fa = this.pcAccountDAO.get(userId, asset);
		if(fa==null){
			return null;
		}
		return fa.getBalance();
	}

	/**
	 * 加钱
	 */
	public Integer add(@RequestBody PcAddRequest request){
		PcAccountRecord record = this.req2record(request);
		
		record.setType(FundFlowDirection.INCOME);
		record.setSn(SnUtils.newRecordSn());
		
		return this.newRecord(record);
	}
	
	/**
	 * 减钱
	 */
	public Integer cut(@RequestBody PcCutRequest request){
		PcAccountRecord record = this.req2record(request);
		
		record.setType(FundFlowDirection.EXPENSES);
		record.setSn(SnUtils.newRecordSn());
		
		return this.newRecord(record);
	}
	
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
			throw new ExSysException(ExCommonError.OBJ_DONT_EXIST);
		}
		return record;
	}

	protected int newRecord(PcAccountRecord record){
		Long now = DbDateUtils.now();
		
		//金额必须是正数
		if(BigUtils.leZero(record.getAmount())){
			throw new ExSysException(ExCommonError.REQUIRE_POSITIVE);
		}
		
		if(this.checkExist(record)){
			logger.warn("重复的请求！");
			return InvokeResult.NOCHANGE;
		}
		
		PcAccount pcAccount = this.pcAccountDAO.getAndLock(record.getUserId(), record.getAsset());
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
			this.updateAccount(pcAccount);
		}
		
		//设置本比余额
		record.setSerialNo(pcAccount.getVersion());
		record.setBalance(pcAccount.getBalance());
		
		//保存记录
		record.setCreated(now);
		record.setModified(now);
		this.fundAccountRecordDAO.save(record);
		
		publisher.publishEvent(record);
	
		return InvokeResult.SUCCESS;
	}
	
	private void updateAccount(PcAccount pcAccount){
		int updatedRows = this.pcAccountDAO.update(pcAccount);
		if(updatedRows==0){
			throw new UpdateException("更新失败");
		}
	}
	
	private void checkBalance(PcAccountRecord record, BigDecimal newBalance){
		//检查余额
		if(FundFlowDirection.EXPENSES==record.getType()){
			if(newBalance.compareTo(BigDecimal.ZERO) < 0){
				throw new ExException(PcAccountError.BALANCE_NOT_ENOUGH);
			}
		}
	}
	
	private PcAccount newPcAccount(Long userId, String asset, BigDecimal balance, Long now){
		PcAccount account = new PcAccount();
		account.setAsset(asset);
		account.setBalance(balance);
		account.setUserId(userId);
		account.setCreated(now);
		account.setModified(now);
		account.setVersion(0L);
		this.pcAccountDAO.save(account);
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
				throw new ExSysException(PcAccountError.INCONSISTENT_REQUESTS, ov, nv);
			}
		}
		
		return true;
	}

	private PcAccountRecord req2record(FundRequest request){
		if(request.getAmount()==null){
			throw new ExSysException(ExCommonError.PARAM_EMPTY);
		}
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

