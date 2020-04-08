package com.hp.sh.expv3.fund.transfer.api;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.fund.transfer.component.FundServiceContext;
import com.hp.sh.expv3.fund.transfer.constant.FundTransferStatus;
import com.hp.sh.expv3.fund.transfer.entity.FundTransfer;
import com.hp.sh.expv3.fund.transfer.error.TransferError;
import com.hp.sh.expv3.fund.transfer.mq.MqSender;
import com.hp.sh.expv3.fund.transfer.mq.msg.NewTransfer;
import com.hp.sh.expv3.fund.transfer.service.FundTransferCoreService;
import com.hp.sh.expv3.pc.error.PcAccountError;
import com.hp.sh.expv3.utils.math.BigUtils;

@RestController
public class FundTransferCoreApiAction implements FundTransferCoreApi {
	private static final Logger logger = LoggerFactory.getLogger(FundTransferCoreApiAction.class);

	@Autowired
	private FundTransferCoreService fundTransferCoreService;
	
	@Autowired
	private FundServiceContext fundServiceContext;
	
	@Autowired
	private MqSender mqSender;

	@Override
	@LockIt(key="transfer-${userId}-${asset}")
	public void transfer(Long userId, String asset, Integer srcAccountType, Integer targetAccountType, BigDecimal amount){
		if(srcAccountType.equals(targetAccountType)){
			throw new ExException(TransferError.SAME_ACCOUNT_TYPE);
		}
		
		//检查余额
		BigDecimal balance = fundServiceContext.getBalance(userId, srcAccountType, asset);
		if(BigUtils.lt(balance, amount)){
			throw new ExException(PcAccountError.BALANCE_NOT_ENOUGH);
		}
		
		//转帐记录
		FundTransfer transfer = fundTransferCoreService.transfer(userId, asset, srcAccountType, targetAccountType, amount);
		
		mqSender.sendMsg(new NewTransfer(transfer.getUserId(), transfer.getId()));
	}

	@Override
	public void handlePending() {
		Page page = new Page(1, 10, 1000L);
		while(true){
			List<FundTransfer> list = this.fundTransferCoreService.findPending(page);
			if(list==null || list.isEmpty()){
				break;
			}
			for(FundTransfer record : list){
				try{
					this.handleOne(record);
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	@Override
	public void handleOne(Long userId, Long id){
		FundTransfer transfer = this.fundTransferCoreService.findById(userId, id);
		this.handleOne(transfer);
	}
	
	private void handleOne(FundTransfer record){
		switch (record.getStatus()) {
		case FundTransferStatus.STATUS_NEW : 
			//扣减源账户
			try{
				fundServiceContext.cutSrcFund(record);
			}catch(Exception e){
				logger.error("处理转账失败", e);
				this.fundTransferCoreService.changeStatus(record, FundTransferStatus.STATUS_FAIL, record.getStatus()+":"+e.getMessage());
				return;
			}
			//修改状态
			this.fundTransferCoreService.changeStatus(record, FundTransferStatus.STATUS_SRC_COMPLETE, null);
		case FundTransferStatus.STATUS_SRC_COMPLETE : 
			//增加目标账户
			fundServiceContext.addTargetFund(record);
			//修改状态
//			this.fundTransferCoreService.changeStatus(record, FundTransfer.STATUS_TARGET_COMPLETE);
		case FundTransferStatus.STATUS_TARGET_COMPLETE : 
			this.fundTransferCoreService.changeStatus(record, FundTransferStatus.STATUS_SUCCESS, null);
		}
	}

}
