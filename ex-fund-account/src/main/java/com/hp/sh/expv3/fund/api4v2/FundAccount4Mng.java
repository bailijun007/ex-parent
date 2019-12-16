package com.hp.sh.expv3.fund.api4v2;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.base.BaseApiAction;
import com.hp.sh.expv3.fund.cash.api.ChainCasehApiAction;
import com.hp.sh.expv3.fund.wallet.api.FundAccountCoreApi;
import com.hp.sh.expv3.fund.wallet.api.FundAccountCoreApiAction;
import com.hp.sh.expv3.fund.wallet.api.constant.TradeType;
import com.hp.sh.expv3.fund.wallet.vo.request.FundAddRequest;
import com.hp.sh.expv3.utils.SnUtils;

@RestController
@RequestMapping("/inner/api/msg/send2")
public class FundAccount4Mng extends BaseApiAction implements FundAccount4MngDef{
	
	@Autowired
	private ChainCasehApiAction chainCasehApiAction;
	
	@Autowired
	private FundAccountCoreApiAction fundAccountCoreApi;

	@Override
	public boolean addAvailableByManager(String operator, long accountId, String asset, BigDecimal delta, String token) {
		FundAddRequest request = new FundAddRequest();
		request.setAmount(delta);
		request.setAsset(asset);
		request.setRemark("后台修改可用余额");
		request.setTradeNo(SnUtils.genRndSn());
		request.setTradeType(delta.doubleValue()>0?TradeType.ADD:TradeType.CUT);
		request.setUserId(accountId);
		fundAccountCoreApi.add(request);
		return true;
	}

	@Override
	public boolean withdrawAuditPass(String operator, long accountId, String asset, long withdrawId, String token) {
		chainCasehApiAction.approve(accountId, withdrawId);
		return true;
	}

	@Override
	public boolean withdrawAuditFail(String operator, long accountId, String asset, long withdrawId, String reason, String token) {
		chainCasehApiAction.reject(accountId, withdrawId, reason);
		return true;
	}


}
