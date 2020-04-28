package com.hp.sh.expv3.pc.trade.service.impl;

import com.hp.sh.expv3.pc.trade.dao.PcKlinePersistentDataMapper;
import com.hp.sh.expv3.pc.trade.pojo.PcKline;
import com.hp.sh.expv3.pc.trade.service.PcKlinePersistentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/4/23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcKlinePersistentDataServiceImpl implements PcKlinePersistentDataService {
    @Autowired
    private PcKlinePersistentDataMapper pcKlinePersistentDataMapper;

    @Override
    public boolean isExist(PcKline bbkLine) {
        Map<String, Object> map = new HashMap<>();
        map.put("asset", bbkLine.getAsset());
        map.put("symbol", bbkLine.getSymbol());
        map.put("timestamp", bbkLine.getTimestamp());
        map.put("frequence", bbkLine.getFrequence());
        PcKline bbKline = pcKlinePersistentDataMapper.queryOne(map);
        if (null == bbKline) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void batchSave(List<PcKline> bbkLineList) {
        if (CollectionUtils.isEmpty(bbkLineList)) {
            return;
        }
        pcKlinePersistentDataMapper.batchSave(bbkLineList);
    }

    @Override
    public void batchUpdate(List<PcKline> bbkLineList) {
        if (CollectionUtils.isEmpty(bbkLineList)) {
            return;
        }
        pcKlinePersistentDataMapper.batchUpdate(bbkLineList);
    }

    @Override
    public void saveOrUpdate(PcKline bbkLine) {
        boolean exist = isExist(bbkLine);
        if (!exist) {
            pcKlinePersistentDataMapper.save(bbkLine);
        }else {
            pcKlinePersistentDataMapper.update(bbkLine);
        }
    }

    @Override
    public Long queryIdByBBKLine(PcKline bbkLine) {
        Map<String, Object> map = new HashMap<>();
        map.put("asset", bbkLine.getAsset());
        map.put("symbol", bbkLine.getSymbol());
        map.put("timestamp", bbkLine.getTimestamp());
        map.put("frequence", bbkLine.getFrequence());
        PcKline bbKline = pcKlinePersistentDataMapper.queryOne(map);
        return bbKline.getId();
    }
}
