/**
 * 
 */
package com.hp.sh.expv3.pc.extension.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.hp.sh.expv3.pc.extension.service.PcAccountExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcAccountExtVo;
import com.hp.sh.expv3.pc.extension.vo.PcAccountVo;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.extension.dao.PcAccountDAO;
import org.springframework.util.CollectionUtils;

/**
 * @author wangjg
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcAccountExtendServiceImpl implements PcAccountExtendService {
	private static final Logger logger = LoggerFactory.getLogger(PcAccountExtendServiceImpl.class);

	@Autowired
	private PcAccountDAO pcAccountDAO;

	@Override
    public BigDecimal getBalance(Long userId, String asset){
        PcAccountVo fa = this.pcAccountDAO.get(userId, asset);
        if(fa==null){
			return null;
		}
		return fa.getBalance();
	}

    @Override
    public PcAccountExtVo findContractAccount(Long userId, String asset) {
        PcAccountExtVo vo=new PcAccountExtVo();
        PcAccountVo pcAccount = pcAccountDAO.get(userId, asset);
        Optional<PcAccountVo> optional = Optional.ofNullable(pcAccount);
        vo.setAccountId(optional.map(u->u.getUserId()).orElse(null));
        vo.setAsset(optional.map(a->a.getAsset()).orElse(null));
        vo.setAvailable(optional.map(a->a.getBalance()).orElse(BigDecimal.ZERO));
        return vo;
    }

    @Override
    public List<PcAccountExtVo> findContractAccountList(Long userId, String asset) {
        List<PcAccountExtVo> list=new ArrayList<>();
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        List<PcAccountVo> pcAccountVos = pcAccountDAO.queryList(map);
        if(!CollectionUtils.isEmpty(pcAccountVos)){
            for (PcAccountVo pcAccountVo : pcAccountVos) {
                PcAccountExtVo extVo = new PcAccountExtVo();
                BeanUtils.copyProperties(pcAccountVo,extVo);
                extVo.setAccountId(pcAccountVo.getUserId());
                extVo.setAsset(pcAccountVo.getAsset());
                extVo.setAvailable(pcAccountVo.getBalance());
                list.add(extVo);
            }
        }
        return list;
    }
}

