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

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.fund.cash.dao.WithdrawalRecordDAO;
import com.hp.sh.expv3.fund.cash.entity.WithdrawalRecord;
import com.hp.sh.expv3.fund.constant.ApprovalStatus;
import com.hp.sh.expv3.fund.constant.PayChannel;
import com.hp.sh.expv3.fund.constant.PaymentStatus;
import com.hp.sh.expv3.fund.constant.SynchStatus;
import com.hp.sh.expv3.fund.wallet.constant.TradeType;
import com.hp.sh.expv3.fund.wallet.service.FundAccountCoreService;
import com.hp.sh.expv3.fund.wallet.vo.request.FundAddRequest;
import com.hp.sh.expv3.fund.wallet.vo.request.FundCutRequest;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.SnUtils;

/**
 * 提现服务
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class WithdrawalService {
	private static final Logger logger = LoggerFactory.getLogger(WithdrawalService.class);

	@Autowired
	private WithdrawalRecordDAO withdrawalRecordDAO;

	public void createWithdrawal(Long userId, String asset, String account, BigDecimal amount, String transactionId, Integer channelId) {
		
		Long now = DbDateUtils.now();
		WithdrawalRecord record = new WithdrawalRecord();
		record.setSn(SnUtils.newDepositSn());
		record.setUserId(userId);
		record.setAsset(asset);
		record.setAccount(account);
		record.setAmount(amount);
		record.setChannelId(channelId);
		record.setCreated(now);
		record.setModified(now);
		
		record.setTransactionId(transactionId);
		record.setPayStatus(PaymentStatus.PENDING);
		record.setPayStatusDesc(null);
		record.setPayTime(null);
		record.setPayFinishTime(null);
		
		record.setSynchStatus(SynchStatus.SYNCH); // 下面同事务执行口扣款，所以这里直接设置为已同步
		
		record.setApprovalStatus(ApprovalStatus.IN_AUDIT);
		
		this.withdrawalRecordDAO.save(record);
		
		this.cutBalance(record);
	}
	
	/**
	 * 批准提现申请
	 * @param userId
	 */
	public WithdrawalRecord approveWithdrawal(Long userId, Long id){
		WithdrawalRecord record = this.withdrawalRecordDAO.findById(userId, id);
		if(record.getApprovalStatus()!=ApprovalStatus.IN_AUDIT){
			return null;
		}
		record.setApprovalStatus(ApprovalStatus.APPROVED);
		record.setModified(DbDateUtils.now());
		this.withdrawalRecordDAO.update(record);
		
		return record;
	}
	
	/**
	 * 拒绝提现申请
	 * @param userId
	 */
	public void rejectWithdrawal(Long userId, Long id){
		WithdrawalRecord record = this.withdrawalRecordDAO.findById(userId, id);
		record.setApprovalStatus(ApprovalStatus.REJECTED);
		record.setSynchStatus(SynchStatus.RETURN);
		this.withdrawalRecordDAO.update(record);
		
		this.returnBalance(record);
	}

	public void onDrawSuccess(Long userId, Long id, String txHash){
		Long now = DbDateUtils.now();
		WithdrawalRecord rr = this.withdrawalRecordDAO.findById(userId, id);
		rr.setTxHash(txHash);
		rr.setPayTime(now);
		rr.setPayFinishTime(now);
		rr.setPayStatusDesc("提现成功");
		rr.setPayStatus(PaymentStatus.SUCCESS);
		this.withdrawalRecordDAO.update(rr);
	}

	public void onDrawFail(Long userId, Long id, String error){
		Long now = DbDateUtils.now();
		WithdrawalRecord rr = this.withdrawalRecordDAO.findById(userId, id);
		rr.setPayTime(now);
		rr.setPayFinishTime(now);
		rr.setPayStatusDesc(error);
		rr.setPayStatus(PaymentStatus.FAIL);
		
		rr.setSynchStatus(SynchStatus.RETURN);
		this.withdrawalRecordDAO.update(rr);
		
		//同步同事务执行
		this.returnBalance(rr);
	}
	
	@CrossDB
	public List<WithdrawalRecord> findPendingWithDrawal(Page page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("approvalStatus", ApprovalStatus.APPROVED);
		params.put("payStatus", PaymentStatus.PENDING);
		List<WithdrawalRecord> list = this.withdrawalRecordDAO.queryList(params);
		return list;
	}
	
	public WithdrawalRecord getWithdrawal(Long userId, Long id){
		WithdrawalRecord record = this.withdrawalRecordDAO.findById(userId, id);
		return record;
	}

	int _______同步余额_________; //因为是同库，使用本地事务
	
	@Deprecated
	private void cutBalance(WithdrawalRecord record){
		FundCutRequest request = new FundCutRequest();
		request.setAsset(record.getAsset());
		request.setAmount(record.getAmount());
		request.setRemark("提现扣款:"+ PayChannel.getName(record.getChannelId()));
		request.setTradeNo(SnUtils.getSynchCutSn(record.getSn()));
		request.setTradeType(TradeType.WITHDRAWAL);
		request.setUserId(record.getUserId());
		
		fundAccountCoreApi.cut(request);
	}
	
	public void synchReturnBalance(WithdrawalRecord rr){
		this.returnBalance(rr);
		rr.setSynchStatus(SynchStatus.SYNCH);
		this.withdrawalRecordDAO.update(rr);
	}
	
	@Deprecated
	private void returnBalance(WithdrawalRecord rr){
		FundAddRequest addRequest = new FundAddRequest();
		addRequest.setAsset(rr.getAsset());
		addRequest.setAmount(rr.getAmount());
		addRequest.setRemark("提现失败返回账户:"+ PayChannel.getName(rr.getChannelId()));
		addRequest.setTradeNo(SnUtils.getSynchReturnSn(rr.getSn()));
		addRequest.setTradeType(TradeType.WITHDRAWAL_RETURN);
		addRequest.setUserId(rr.getUserId());
		fundAccountCoreApi.add(addRequest);
	}
	
	@Autowired
	private FundAccountCoreService fundAccountCoreApi;

	public List<WithdrawalRecord> findPendingSynch(Page page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("payStatus", PaymentStatus.SUCCESS);
		params.put("synchStatus", SynchStatus.NO);
		List<WithdrawalRecord> list = this.withdrawalRecordDAO.queryList(params);
		return list;
	}

}
