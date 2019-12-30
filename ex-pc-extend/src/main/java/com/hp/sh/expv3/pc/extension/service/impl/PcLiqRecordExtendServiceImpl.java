package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.dao.PcLiqRecordDAO;
import com.hp.sh.expv3.pc.extension.service.PcLiqRecordExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcLiqRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/25
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcLiqRecordExtendServiceImpl implements PcLiqRecordExtendService {
    @Autowired
    private PcLiqRecordDAO pcLiqRecordDAO;

    @Override
    public PcLiqRecordVo getPcLiqRecord(Long refId, String asset, String symbol, Long userId, Long time) {
        Map<String, Object> map=new HashMap<>();
        map.put("id",refId);
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("userId",userId);
        map.put("createdBegin",time);
        PcLiqRecordVo pcLiqRecordVo = pcLiqRecordDAO.queryOne(map);
        return pcLiqRecordVo;
    }
}
