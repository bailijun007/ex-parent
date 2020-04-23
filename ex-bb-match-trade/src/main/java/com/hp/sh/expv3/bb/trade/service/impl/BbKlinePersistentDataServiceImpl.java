package com.hp.sh.expv3.bb.trade.service.impl;

import com.hp.sh.expv3.bb.trade.dao.BbKlinePersistentDataMapper;
import com.hp.sh.expv3.bb.trade.pojo.BBKLine;
import com.hp.sh.expv3.bb.trade.service.BbKlinePersistentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/4/23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbKlinePersistentDataServiceImpl implements BbKlinePersistentDataService {
    @Autowired
    private BbKlinePersistentDataMapper bbKlinePersistentDataMapper;

    @Override
    public boolean isExist(BBKLine bbkLine) {
        Map<String, Object> map = new HashMap<>();
        map.put("asset", bbkLine.getAsset());
        map.put("symbol", bbkLine.getSymbol());
        map.put("timestamp", bbkLine.getTimestamp());
        map.put("frequence", bbkLine.getFrequence());
        BBKLine bbKline = bbKlinePersistentDataMapper.queryOne(map);
        if (null == bbKline) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void batchSave(List<BBKLine> bbkLineList) {
        if (CollectionUtils.isEmpty(bbkLineList)) {
            return;
        }
        bbKlinePersistentDataMapper.batchSave(bbkLineList);
    }

    @Override
    public void batchUpdate(List<BBKLine> bbkLineList) {
        if (CollectionUtils.isEmpty(bbkLineList)) {
            return;
        }
        bbKlinePersistentDataMapper.batchUpdate(bbkLineList);
    }

    /**
     * 如果存在 就更新
     * 如果不存在，就新增
     *
     * @param bbkLine
     */
    @Override
    public void saveOrUpdate(BBKLine bbkLine) {
        boolean exist = isExist(bbkLine);
        if (!exist) {
            bbKlinePersistentDataMapper.save(bbkLine);
        }else {
            bbKlinePersistentDataMapper.update(bbkLine);
        }
    }

    @Override
    public Long queryIdByBBKLine(BBKLine bbkLine) {
        Map<String, Object> map = new HashMap<>();
        map.put("asset", bbkLine.getAsset());
        map.put("symbol", bbkLine.getSymbol());
        map.put("timestamp", bbkLine.getTimestamp());
        map.put("frequence", bbkLine.getFrequence());
        BBKLine bbKline = bbKlinePersistentDataMapper.queryOne(map);
        return bbKline.getId();
    }
}
