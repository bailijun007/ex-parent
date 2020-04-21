/**
 * 
 */
package com.hp.sh.expv3.bb.module.account.service;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.bb.constant.BBAccountRecordType;
import com.hp.sh.expv3.bb.error.BBAccountError;
import com.hp.sh.expv3.bb.module.account.dao.BBAccountDAO;
import com.hp.sh.expv3.bb.module.account.dao.BBAccountRecordDAO;
import com.hp.sh.expv3.bb.module.account.entity.BBAccount;
import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecord;
import com.hp.sh.expv3.bb.vo.request.BBAddRequest;
import com.hp.sh.expv3.bb.vo.request.BBCutRequest;
import com.hp.sh.expv3.bb.vo.request.FreezeRequest;
import com.hp.sh.expv3.bb.vo.request.FundRequest;
import com.hp.sh.expv3.bb.vo.request.ReleaseFrozenRequest;
import com.hp.sh.expv3.bb.vo.request.UnFreezeRequest;
import com.hp.sh.expv3.commons.ctx.TxContext;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.config.db.SnGenerator;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.error.ExCommonError;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * @author wangjg
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class BBAccountCoreService{
	private static final Logger logger = LoggerFactory.getLogger(BBAccountCoreService.class);

	@Autowired
	private BBAccountDAO accountDAO;

	@Autowired
	private BBAccountRecordDAO fundAccountRecordDAO;
	
	@Autowired
	private SnGenerator generator;
    
    @Autowired
    private ApplicationEventPublisher publisher;
	
	public Boolean exist(Long userId, String asset){
		BBAccount fa = this.accountDAO.get(userId, asset);
		return fa!=null;
	}

	public int createAccount(Long userId, String asset){
		BBAccount fa = this.accountDAO.get(userId, asset);
		if(fa!=null){
			return InvokeResult.NOCHANGE;
		}
		this.newAccount(userId, asset, BigDecimal.ZERO, DbDateUtils.now());
		
		return InvokeResult.SUCCESS;
	}
	
	public BigDecimal getBalance(Long userId, String asset){
		BBAccount fa = this.accountDAO.get(userId, asset);
		if(fa==null){
			return null;
		}
		return fa.getBalance();
	}

	/**
	 * 加钱
	 */
	public Integer add(@RequestBody BBAddRequest request){
		//检查请求
		this.checkRequest(request);
		
		BBAccountRecord record = this.req2record(request);
		record.setType(BBAccountRecordType.ADD);
		
		//检查重复
		if(this.checkExist(record)){
			logger.warn("重复的请求！");
			return InvokeResult.NOCHANGE;
		}
		//保存总账
		BBAccount account = this.getAccount(record.getUserId(), record.getAsset());
		account.setBalance(account.getBalance().add(record.getAmount()));
		this.updateAccount(account, record);
		
		return InvokeResult.SUCCESS;
	}
	
	/**
	 * 减钱
	 */
	public Integer cut(@RequestBody BBCutRequest request){
		//检查请求
		this.checkRequest(request);
		
		BBAccountRecord record = this.req2record(request);
		record.setType(BBAccountRecordType.CUT);
		
		//检查重复
		if(this.checkExist(record)){
			logger.warn("重复的请求！");
			return InvokeResult.NOCHANGE;
		}
		//保存总账
		BBAccount account = this.getAccount(record.getUserId(), record.getAsset());
		account.setBalance(account.getBalance().subtract(record.getAmount()));
		this.updateAccount(account, record);
		
		return InvokeResult.SUCCESS;
	}

	/**
	 * 冻结
	 * @param userId
	 * @param asset
	 * @param amount
	 * @return
	 */
	public Integer freeze(FreezeRequest request){
		//检查请求
		this.checkRequest(request);
		
		BBAccountRecord record = this.req2record(request);
		record.setType(BBAccountRecordType.FROZEN);
		
		//检查重复
		if(this.checkExist(record)){
			logger.warn("重复的请求！");
			return InvokeResult.NOCHANGE;
		}
		
		//保存总账
		BBAccount account = this.getAccount(record.getUserId(), record.getAsset());
		account.setBalance(account.getBalance().subtract(record.getAmount()));
		account.setFrozen(account.getFrozen().add(record.getAmount()));
		this.updateAccount(account, record);
		
		return InvokeResult.SUCCESS;
	}
	
	/**
	 * 解冻
	 * @param request
	 * @return
	 */
	public Integer unfreeze(UnFreezeRequest request){
		//检查请求
		this.checkRequest(request);
		
		BBAccountRecord record = this.req2record(request);
		record.setType(BBAccountRecordType.UNFROZEN);
		
		//检查重复
		if(this.checkExist(record)){
			logger.warn("重复的请求！");
			return InvokeResult.NOCHANGE;
		}
		
		//保存总账
		BBAccount account = this.getAccount(record.getUserId(), record.getAsset());
		account.setBalance(account.getBalance().add(record.getAmount()));
		account.setFrozen(account.getFrozen().subtract(record.getAmount()));
		this.updateAccount(account, record);
		
		return InvokeResult.SUCCESS;
	}
	
	/**
	 * 释放冻结
	 * @param request
	 * @return
	 */
	public Integer release(ReleaseFrozenRequest request){
		//检查请求
		this.checkRequest(request);
		
		BBAccountRecord record = this.req2record(request);
		record.setType(BBAccountRecordType.RELEASE);
		
		//检查重复
		if(this.checkExist(record)){
			logger.warn("重复的请求！");
			return InvokeResult.NOCHANGE;
		}
		
		//保存总账
		BBAccount account = this.getAccount(record.getUserId(), record.getAsset());
		account.setFrozen(account.getFrozen().subtract(record.getAmount()));
		this.updateAccount(account, record);
		
		return InvokeResult.SUCCESS;
	}
	
	public Boolean checkTradNo(Long userId, String tradeNo) {
		BBAccountRecord rcd = this.fundAccountRecordDAO.findByTradeNo(userId, tradeNo);
		if (rcd == null) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	private BBAccount getAccount(Long userId, String asset){
		BBAccount account = this.accountDAO.get(userId, asset);
		if(account==null){
			account = new BBAccount();
			account.setAsset(asset);
			account.setBalance(BigDecimal.ZERO);
			account.setFrozen(BigDecimal.ZERO);
			account.setTotal(BigDecimal.ZERO);
			account.setUserId(userId);
			account.setVersion(0L);
			account.setCreated(-1L);
		}
		return account;
	}
	
	private void updateAccount(BBAccount account, BBAccountRecord record){
		Long now = DbDateUtils.now();

		this.checkBalance(account, record);
		
		account.setTotal(account.getBalance().add(account.getFrozen()));
		account.setModified(now);
		
		if(account.getCreated() == -1L){
			account.setCreated(now);
			this.accountDAO.save(account);
		}else{
			int updatedRows = this.accountDAO.update(account);
			if(updatedRows==0){
				throw new UpdateException("更新失败");
			}
		}
		record.setRemark(record.getRemark());
		//保存本笔明细
		this.saveRecord(record, account);
	}
	
	private void saveRecord(BBAccountRecord record, BBAccount account){
		//设置本笔余额
		record.setSerialNo(account.getVersion());
		record.setBalance(account.getBalance());
		record.setFrozen(account.getFrozen());
		record.setTotal(account.getTotal());
		
		//保存记录
		record.setSn( generator.genSn(record) );
		record.setCreated(account.getModified());
		record.setModified(account.getModified());
		this.fundAccountRecordDAO.save(record);
		
		publisher.publishEvent(record);
	}
	
	private void checkBalance(BBAccount account, BBAccountRecord record){
		//检查余额
		if(account.getBalance().compareTo(BigDecimal.ZERO) < 0){
			throw new ExException(BBAccountError.BALANCE_NOT_ENOUGH, "balance", record);
		}
		//检查冻结
//		if(account.getFrozen().compareTo(BigDecimal.ZERO) < 0){
//			logger.error("冻结金额小于0：{}", record);
//			throw new ExException(BBAccountError.FROZEN_NOT_ENOUGH, "frozen", record);
//		}
	}
	
	private BBAccount newAccount(Long userId, String asset, BigDecimal balance, Long now){
		BBAccount account = new BBAccount();
		account.setAsset(asset);
		account.setBalance(balance);
		account.setFrozen(BigDecimal.ZERO);
		account.setTotal(balance);
		account.setUserId(userId);
		account.setCreated(now);
		account.setModified(now);
		account.setVersion(0L);
		this.accountDAO.save(account);
		return account;
	}

	private boolean checkExist(BBAccountRecord record) {
		
		//金额必须是正数
		if(record.getAmount().compareTo(BigDecimal.ZERO)<=0){
			throw new ExSysException(ExCommonError.REQUIRE_POSITIVE_AMOUNT, record);
		}
		
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

	private void checkRequest(FundRequest request){
		if(StringUtils.isBlank(request.getAsset())){
			throw new ExException(ExCommonError.PARAM_EMPTY, request); 
		}
		if(StringUtils.isBlank(request.getRemark())){
			throw new ExException(ExCommonError.PARAM_EMPTY, request); 
		}
		if(StringUtils.isBlank(request.getTradeNo())){
			throw new ExException(ExCommonError.PARAM_EMPTY, request); 
		}
		if(request.getAmount()==null){
			throw new ExException(ExCommonError.PARAM_EMPTY, request); 
		}
		if(request.getAssociatedId()==null){
			throw new ExException(ExCommonError.PARAM_EMPTY, request); 
		}
		if(request.getTradeType()==null){
			throw new ExException(ExCommonError.PARAM_EMPTY, request); 
		}
		if(request.getUserId()==null){
			throw new ExException(ExCommonError.PARAM_EMPTY, request); 
		}
		//金额必须是正数
		if(request.getAmount().compareTo(BigDecimal.ZERO)<=0){
			throw new ExSysException(ExCommonError.REQUIRE_POSITIVE_AMOUNT, request);
		}
	}

	private BBAccountRecord req2record(FundRequest request){
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

