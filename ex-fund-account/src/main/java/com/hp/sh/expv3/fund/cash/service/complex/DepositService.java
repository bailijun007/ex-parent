package com.hp.sh.expv3.fund.cash.service.complex;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.error.ExCommonError;
import com.hp.sh.expv3.error.ExSysError;
import com.hp.sh.expv3.fund.cash.dao.DepositRecordDAO;
import com.hp.sh.expv3.fund.cash.entity.DepositRecord;
import com.hp.sh.expv3.fund.constant.PayChannel;
import com.hp.sh.expv3.fund.constant.PaymentStatus;
import com.hp.sh.expv3.fund.constant.SynchStatus;
import com.hp.sh.expv3.fund.wallet.constant.TradeType;
import com.hp.sh.expv3.fund.wallet.service.FundAccountCoreService;
import com.hp.sh.expv3.fund.wallet.vo.request.FundAddRequest;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.SnUtils;

/**
 * 充值提现服务
 * @author wangjg
 *
 */
@Service
@Transactional
public class DepositService {
	private static final Logger logger = LoggerFactory.getLogger(DepositService.class);

	@Autowired
	private DepositRecordDAO depositRecordDAO;
	
	public String deposit(Long userId, String asset, String account, BigDecimal amount, String chainOrderId, Integer channelId, String txHash) {
		this.checkExist(userId, asset, chainOrderId, channelId);
		Long now = DbDateUtils.now();
		DepositRecord rr = new DepositRecord();

		rr.setSn(SnUtils.newDepositSn());
		rr.setUserId(userId);
		rr.setAsset(asset);
		rr.setAccount(account);
		rr.setAmount(amount);
		rr.setCreated(now);
		rr.setModified(now);

		rr.setChannelId(channelId);
		rr.setPayStatus(PaymentStatus.PENDING);
		rr.setTransactionId(chainOrderId);
		rr.setPayTime(now);
		rr.setPayFinishTime(null);
		rr.setPayStatusDesc(null);
		
		rr.setSynchStatus(SynchStatus.NO);
		
		rr.setTxHash(txHash);
		
		depositRecordDAO.save(rr);
		return rr.getSn();
	}
	
	private void checkExist(Long userId, String asset, String chainOrderId, Integer channelId) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("asset", asset);
		params.put("channelId", channelId);
		params.put("transactionId", chainOrderId);
		Long count = this.depositRecordDAO.queryCount(params);
		if(count>0){
			throw new ExSysException(ExCommonError.REPEAT_ORDER);
		}
	}

	public void onPaySuccess(Long userId, String sn){
		DepositRecord rr = this.depositRecordDAO.findBySn(userId, sn);
		if(rr==null){
			throw new ExException(ExCommonError.OBJ_DONT_EXIST);
		}
		
		if(rr.getPayStatus()==PaymentStatus.SUCCESS){
			logger.warn("已经处理过了,sn={}", sn);
			return;
		}
		
		if(rr.getPayStatus()==PaymentStatus.FAIL){
			throw new RuntimeException("见鬼了！");
		}
		
		if(rr.getPayStatus()==PaymentStatus.PENDING){
			this.changePayStatus(rr, PaymentStatus.SUCCESS);
		}
		
		if(rr.getPayStatus()==PaymentStatus.SUCCESS && rr.getSynchStatus()!=SynchStatus.SYNCH){
			this.synchBalance(rr);
		}
	}
	
	public void onPayFail(Long userId, String sn){
		DepositRecord rr = this.depositRecordDAO.findBySn(userId, sn);
		if(rr==null){
			throw new ExException(CommonError.OBJ_DONT_EXIST);
		}
		
		if(rr.getPayStatus()==PaymentStatus.FAIL){
			logger.warn("已经处理过了,sn={}", sn);
			return;
		}
		
		if(rr.getPayStatus()==PaymentStatus.SUCCESS){
			throw new ExSysException(ExSysError.BIZ_LOGIC_ERR);
		}
		
		this.changePayStatus(rr, PaymentStatus.FAIL);
	}
	
	public List<DepositRecord> findPendingPay(Page page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("payStatus", PaymentStatus.PENDING);
		List<DepositRecord> list = this.depositRecordDAO.queryList(params);
		return list;
	}

	public void changePayStatus(DepositRecord rr, int status){
		Long now = DbDateUtils.now();
		if(status==PaymentStatus.SUCCESS){
			rr.setPayFinishTime(now);
		}
		rr.setPayStatus(status);
		rr.setModified(now);
		this.depositRecordDAO.update(rr);
	}
	
	@Deprecated
	public void synchBalance(DepositRecord rr){
		FundAddRequest addRequest = new FundAddRequest();
		addRequest.setAsset(rr.getAsset());
		addRequest.setAmount(rr.getAmount());
		addRequest.setRemark("充值:"+ PayChannel.getName(rr.getChannelId()));
		addRequest.setTradeNo(SnUtils.getSynchAddSn(rr.getSn()));
		addRequest.setTradeType(TradeType.DEPOSIT);
		addRequest.setUserId(rr.getUserId());
		fundAccountCoreApi.add(addRequest);
		
		rr.setSynchStatus(SynchStatus.SYNCH);
		rr.setModified(DbDateUtils.now());
		this.depositRecordDAO.update(rr);
	}
	
	@Autowired
	private FundAccountCoreService fundAccountCoreApi;

	public List<DepositRecord> findPendingSynch(Page page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("payStatus", PaymentStatus.SUCCESS);
		params.put("synchStatus", SynchStatus.NO);
		List<DepositRecord> list = this.depositRecordDAO.queryList(params);
		return list;
	}
	
}
