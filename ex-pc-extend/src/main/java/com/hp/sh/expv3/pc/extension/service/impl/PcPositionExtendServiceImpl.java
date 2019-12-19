package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcPositionExtendServiceImpl implements PcPositionExtendService {
    @Autowired
    private PcPositionDAO pcPositionDAO;

    @Override
    public BigDecimal getPosMargin(Long userId, String asset) {
        if(userId==null){
            return BigDecimal.ZERO;
        }
        return pcPositionDAO.getPosMargin(userId,asset);
    }
}
