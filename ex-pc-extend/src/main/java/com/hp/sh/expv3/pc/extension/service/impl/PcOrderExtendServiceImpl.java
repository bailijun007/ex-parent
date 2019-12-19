package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcOrderExtendServiceImpl implements PcOrderExtendService {
    @Autowired
    private PcOrderDAO pcOrderDAO;

    @Override
    public BigDecimal getGrossMargin(Long userId, String asset) {
       if(userId==null){
           return BigDecimal.ZERO;
       }
        return pcOrderDAO.getGrossMargin(userId,asset);
    }
}
