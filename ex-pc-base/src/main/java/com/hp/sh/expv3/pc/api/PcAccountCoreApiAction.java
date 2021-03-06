/**
 * 
 */
package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.pc.module.account.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.vo.request.PcAddRequest;
import com.hp.sh.expv3.pc.vo.request.PcCutRequest;

/**
 * 永续合约账户核心接口
 * @author wangjg
 */
@RestController
public class PcAccountCoreApiAction implements PcAccountCoreApi {

	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	
	@Override
	public int createAccount(Long userId, String asset) {
		return pcAccountCoreService.createAccount(userId, asset);
	}

	@Override
	public boolean accountExist(Long userId, String asset) {
		return pcAccountCoreService.exist(userId, asset);
	}

	@Override
	@LockIt(key="U-${request.userId}")
	public Integer add(@RequestBody PcAddRequest request) {
		return pcAccountCoreService.add(request);
	}

	@Override
	@LockIt(key="U-${request.userId}")
	public Integer cut(@RequestBody PcCutRequest request) {
		return pcAccountCoreService.cut(request);
	}

	@Override
	public Boolean checkTradNo(Long userId, String tradeNo) {
		return pcAccountCoreService.checkTradNo(userId, tradeNo);
	}

	@Override
	public BigDecimal getBalance(Long userId, String asset) {
		return pcAccountCoreService.getBalance(userId, asset);
	}
	
}