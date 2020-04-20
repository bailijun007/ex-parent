/**
 * 
 */
package com.hp.sh.expv3.bb.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.bb.api.BBAccountCoreApi;
import com.hp.sh.expv3.bb.module.account.service.BBAccountCoreService;
import com.hp.sh.expv3.bb.vo.request.BBAddRequest;
import com.hp.sh.expv3.bb.vo.request.BBCutRequest;
import com.hp.sh.expv3.commons.lock.LockIt;

/**
 * 币币账户核心接口
 * @author wangjg
 */
@RestController
public class BBAccountCoreApiAction implements BBAccountCoreApi {

	@Autowired
	private BBAccountCoreService bBAccountCoreService;
	
	@Override
	public int createAccount(Long userId, String asset) {
		return bBAccountCoreService.createAccount(userId, asset);
	}

	@Override
	public boolean accountExist(Long userId, String asset) {
		return bBAccountCoreService.exist(userId, asset);
	}

	@Override
	@LockIt(key="U-${request.userId}")
	public Integer add(@RequestBody BBAddRequest request) {
		return bBAccountCoreService.add(request);
	}

	@Override
	@LockIt(key="U-${request.userId}")
	public Integer cut(@RequestBody BBCutRequest request) {
		return bBAccountCoreService.cut(request);
	}

	@Override
	public Boolean checkTradNo(Long userId, String tradeNo) {
		return bBAccountCoreService.checkTradNo(userId, tradeNo);
	}

	@Override
	public BigDecimal getBalance(Long userId, String asset) {
		return bBAccountCoreService.getBalance(userId, asset);
	}
	
}