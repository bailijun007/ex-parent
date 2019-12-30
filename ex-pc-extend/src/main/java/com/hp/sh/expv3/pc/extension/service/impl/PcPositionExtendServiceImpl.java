package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.extension.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.extension.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcPositionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcPositionExtendServiceImpl implements PcPositionExtendService {
    @Autowired
    private PcPositionDAO pcPositionDAO;

    @Autowired
    private PcOrderTradeDAO pcOrderTradeDAO;

    @Override
    public BigDecimal getPosMargin(Long userId, String asset) {
        if (userId == null) {
            return BigDecimal.ZERO;
        }
        return pcPositionDAO.getPosMargin(userId, asset);
    }

    @Override
    public BigDecimal getPl(Long userId, String asset, Long posId) {
        BigDecimal bigDecimal = pcOrderTradeDAO.getPl(userId, asset, posId);
        return bigDecimal;
    }

    @Override
    public BigDecimal getPlRatio(Long userId, String asset, Long posId) {
        BigDecimal pl = this.getPl(userId, asset, posId);
        BigDecimal initMargin = pcPositionDAO.getInitMargin(userId, asset, posId);
        if(initMargin.compareTo(new BigDecimal(0))==0){
            //初始保证金不能为0
            throw new ExException(PcCommonErrorCode.INIT_MARGIN_NOT_EQUAL_ZERO);
        }
        return pl.divide(initMargin);
    }

    @Override
    public List<PcPositionVo> findPositionList(Long userId, String asset, String symbol,Long posId,Integer liqStatus) {
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("id",posId);
        map.put("liqStatus",liqStatus);
        List<PcPositionVo> pcPositionVos = pcPositionDAO.queryList(map);
        return pcPositionVos;
    }
}
