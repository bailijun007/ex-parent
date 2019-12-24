package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeService;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcOrderTradeServiceImpl implements PcOrderTradeService {
    @Autowired
    private PcOrderTradeDAO pcOrderTradeDAO;

    @Override
    public BigDecimal getRealisedPnl(Long posId, Long userId) {
        return pcOrderTradeDAO.getRealisedPnl(posId,userId,null);
    }

    @Override
    public List<PcOrderTradeVo> queryOrderTrade(Long userId, String asset, String symbol, String orderId) {
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("orderId",orderId);
        List<PcOrderTradeVo> voList = pcOrderTradeDAO.queryList(map);
        return voList;
    }
}
