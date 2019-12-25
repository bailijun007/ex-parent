package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.dao.PcAccountSymbolDAO;
import com.hp.sh.expv3.pc.extension.service.PcAccountSymbolExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcAccountSymbolVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcAccountSymbolExtendServiceImpl implements PcAccountSymbolExtendService {
    @Autowired
    private PcAccountSymbolDAO pcAccountSymbolDAO;

    @Override
    public PcAccountSymbolVo getPcAccountSymbol(Long userId, String asset, String symbol) {
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        map.put("symbol",symbol);
        PcAccountSymbolVo symbolVo = pcAccountSymbolDAO.queryOne(map);
        return symbolVo;
    }
}
