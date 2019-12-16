/**
 * 
 */
package com.hp.sh.expv3.pc.extension.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.extension.service.PcAccountCoreService;

/**
 * 永续合约账户核心接口
 * @author wangjg
 */
@RestController
public class PcAccountExtendApiAction implements PcAccountExtendApi {

	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	
	@Override
	public BigDecimal getBalance(Long userId, String asset) {
		return pcAccountCoreService.getBalance(userId, asset);
	}
	
}
