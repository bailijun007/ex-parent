package com.hp.sh.expv3.fund.cash.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.fund.cash.entity.DepositAddr;
import com.hp.sh.expv3.fund.cash.service.DepositAddrService;

import io.swagger.annotations.Api;

/**
 * 提币地址接口
 * @author wangjg
 *
 */
@Api(tags="提币地址接口")
@RestController
public class DepositAddrApiAction implements DepositAddrApi {
	private static final Logger logger = LoggerFactory.getLogger(DepositAddrApiAction.class);
	
	@Autowired
	private DepositAddrService depositAddrService;

	@Override
	public boolean updateRemark(long userId, String asset, long depositAddrId, String remark) {
		DepositAddr depositAddr = depositAddrService.findById(userId, depositAddrId);
		depositAddr.setRemark(remark);
		this.depositAddrService.update(depositAddr);
		return true;
	}

}
