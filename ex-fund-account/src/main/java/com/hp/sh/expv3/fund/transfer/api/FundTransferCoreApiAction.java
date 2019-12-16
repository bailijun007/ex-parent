package com.hp.sh.expv3.fund.transfer.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.transfer.constant.AccountType;
import com.hp.sh.expv3.fund.transfer.constant.TransferError;
import com.hp.sh.expv3.fund.transfer.entity.FundTransfer;
import com.hp.sh.expv3.fund.transfer.mq.MqSender;
import com.hp.sh.expv3.fund.transfer.mq.msg.NewTransfer;
import com.hp.sh.expv3.fund.transfer.service.FundTransferCoreService;
import com.hp.sh.expv3.fund.wallet.api.FundAccountCoreApi;
import com.hp.sh.expv3.fund.wallet.api.constant.TradeType;
import com.hp.sh.expv3.fund.wallet.vo.request.FundAddRequest;
import com.hp.sh.expv3.fund.wallet.vo.request.FundCutRequest;
import com.hp.sh.expv3.pc.api.PcAccountCoreApi;
import com.hp.sh.expv3.pc.constant.PcAccountTradeType;

import io.swagger.annotations.Api;

@Api(tags="资金划转接口")
@RestController
public class FundTransferCoreApiAction implements FundTransferCoreApi {

	@Autowired
	private FundAccountCoreApi fundAccountCoreApi;
	
	@Autowired
	private PcAccountCoreApi pcAccountCoreApi;

	@Autowired
	private FundTransferCoreService fundTransferCoreService;
	
	@Autowired
	private MqSender mqSender;

	@Override
	public void transfer(Long userId, String asset, Integer srcAccountType, Integer targetAccountType, BigDecimal amount) throws Exception{
		if(srcAccountType.equals(targetAccountType)){
			throw new ExException(TransferError.ACCOUNT_TYPE);
		}
		
		FundTransfer transfer = fundTransferCoreService.transfer(userId, asset, srcAccountType, targetAccountType, amount);
		
		mqSender.send(new NewTransfer(transfer.getUserId(), transfer.getId()));
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
				this.handleOne(record);
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
		case FundTransfer.STATUS_NEW:
			//扣减源账户
			if(record.getSrcAccountType()==AccountType.FUND){
				this.cutFund(record);
			}else if(record.getSrcAccountType()==AccountType.PC){
				this.cutPcFund(record);
			}
			//修改状态
			this.fundTransferCoreService.changeStatus(record, FundTransfer.STATUS_SRC_COMPLETE);
		case FundTransfer.STATUS_SRC_COMPLETE:
			//增加目标账户
			if(record.getTargetAccountType()==AccountType.FUND){
				this.addFund(record);
			}else if(record.getTargetAccountType()==AccountType.PC){
				this.addPcFund(record);
			}
//			this.fundTransferCoreService.changeStatus(record, FundTransfer.STATUS_SUCCESS);
		case FundTransfer.STATUS_TARGET_COMPLETE:
			this.fundTransferCoreService.changeStatus(record, FundTransfer.STATUS_SUCCESS);
		}
	}

	private void addFund(FundTransfer record){
		FundAddRequest request = new FundAddRequest();
		request.setAmount(record.getAmount());
		request.setAsset(record.getAsset());
		request.setRemark(record.getRemark());
		request.setTradeNo(record.getSn());
		request.setTradeType(TradeType.TRANSFER_IN);
		request.setUserId(record.getUserId());
		fundAccountCoreApi.add(request);
	}

	private void addPcFund(FundTransfer record){
		com.hp.sh.expv3.pc.vo.request.PcAddRequest request = new com.hp.sh.expv3.pc.vo.request.PcAddRequest();
		request.setAmount(record.getAmount());
		request.setAsset(record.getAsset());
		request.setRemark(record.getRemark());
		request.setTradeNo(record.getSn());
		request.setTradeType(PcAccountTradeType.FUND_TO_PC);
		request.setUserId(record.getUserId());
		pcAccountCoreApi.add(request);
	}

	private void cutFund(FundTransfer record){
		FundCutRequest request = new FundCutRequest();
		request.setAmount(record.getAmount());
		request.setAsset(record.getAsset());
		request.setRemark(record.getRemark());
		request.setTradeNo(record.getSn());
		request.setTradeType(TradeType.TRANSFER_IN);
		request.setUserId(record.getUserId());
		fundAccountCoreApi.cut(request);
	}

	private void cutPcFund(FundTransfer record){
		com.hp.sh.expv3.pc.vo.request.PcCutRequest request = new com.hp.sh.expv3.pc.vo.request.PcCutRequest();
		request.setAmount(record.getAmount());
		request.setAsset(record.getAsset());
		request.setRemark(record.getRemark());
		request.setTradeNo(record.getSn());
		request.setTradeType(PcAccountTradeType.PC_TO_FUND);
		request.setUserId(record.getUserId());
		pcAccountCoreApi.cut(request);
	}
	
}
