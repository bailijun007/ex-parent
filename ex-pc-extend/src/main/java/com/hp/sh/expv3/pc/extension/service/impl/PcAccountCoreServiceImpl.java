/**
 * 
 */
package com.hp.sh.expv3.pc.extension.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import com.hp.sh.expv3.pc.extension.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.extension.vo.PcAccountExtVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.extension.dao.PcAccountDAO;
import com.hp.sh.expv3.pc.module.account.entity.PcAccount;

/**
 * @author wangjg
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcAccountCoreServiceImpl implements PcAccountCoreService {
	private static final Logger logger = LoggerFactory.getLogger(PcAccountCoreServiceImpl.class);

	@Autowired
	private PcAccountDAO pcAccountDAO;

	@Override
    public BigDecimal getBalance(Long userId, String asset){
		PcAccount fa = this.pcAccountDAO.get(userId, asset);
		if(fa==null){
			return null;
		}
		return fa.getBalance();
	}

    @Override
    public PcAccountExtVo findContractAccount(Long userId, String asset) {
        PcAccountExtVo vo=new PcAccountExtVo();
        PcAccount pcAccount = pcAccountDAO.get(userId, asset);
        Optional<PcAccount> optional = Optional.ofNullable(pcAccount);
        vo.setAccountId(optional.map(u->u.getUserId()).orElse(null));
        vo.setAsset(optional.map(a->a.getAsset()).orElse(null));
        vo.setAvailable(optional.map(a->a.getBalance()).orElse(BigDecimal.ZERO));
        return vo;
    }
}

