package com.hp.sh.expv3.bb.trade.service;

import com.hp.sh.expv3.bb.trade.pojo.BBKLine;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/4/23
 */
public interface BbKlinePersistentDataService {
    boolean isExist(BBKLine bbkLine);

    void batchSave(List<BBKLine> bbkLineList);

    void batchUpdate(List<BBKLine> bbkLineList);

    void saveOrUpdate(BBKLine bbkLine);

    Long queryIdByBBKLine(BBKLine bbkLine);
}
