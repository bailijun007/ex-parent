package com.hp.sh.expv3.pc.trade.service;

import com.hp.sh.expv3.pc.trade.pojo.PcKline;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/4/23
 */
public interface PcKlinePersistentDataService {
    boolean isExist(PcKline bbkLine);

    void batchSave(List<PcKline> bbkLineList);

    void batchUpdate(List<PcKline> bbkLineList);

    void saveOrUpdate(PcKline bbkLine);

    Long queryIdByBBKLine(PcKline bbkLine);
}
