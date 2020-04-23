package com.hp.sh.expv3.pc.trade.dao;

import com.hp.sh.expv3.pc.trade.pojo.PcKline;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/4/23
 */
public interface PcKlinePersistentDataMapper {
    void batchSave(@Param("pcKlineDataList") List<PcKline> bbKlineDataList);

    void batchUpdate(@Param("pcKlineDataList") List<PcKline> pcKlineDataList);

    PcKline queryOne(Map<String, Object> map);

    void save(PcKline bbkLine);

    void update(PcKline bbkLine);
}
