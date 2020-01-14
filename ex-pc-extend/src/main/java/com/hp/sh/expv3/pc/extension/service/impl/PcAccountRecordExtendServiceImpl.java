package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.dao.PcAccountRecordDAO;
import com.hp.sh.expv3.pc.extension.service.PcAccountRecordExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcAccountRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcAccountRecordExtendServiceImpl implements PcAccountRecordExtendService {
    @Autowired
    private PcAccountRecordDAO pcAccountRecordDAO;


    @Override
    public PcAccountRecordVo getPcAccountRecord(Long refId, String asset, Long userId, Long time) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", refId);
        map.put("asset", asset);
        map.put("userId", userId);
        map.put("createdBegin", time);
        PcAccountRecordVo pcAccountRecordVo = pcAccountRecordDAO.queryOne(map);

        return pcAccountRecordVo;
    }

    @Override
    public List<PcAccountRecordVo> listPcAccountRecord(List<Long> refIds, String asset, Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("idList", refIds);
        map.put("asset", asset);
        map.put("userId", userId);
        return pcAccountRecordDAO.queryList(map);
    }
}
