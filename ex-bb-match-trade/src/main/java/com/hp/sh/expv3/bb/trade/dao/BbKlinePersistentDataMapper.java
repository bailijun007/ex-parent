package com.hp.sh.expv3.bb.trade.dao;

import com.hp.sh.expv3.bb.trade.pojo.BBKLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/4/23
 */
public interface BbKlinePersistentDataMapper {
    void batchSave(@Param("bbKlineDataList") List<BBKLine> bbKlineDataList);

    void batchUpdate(@Param("bbKlineDataList") List<BBKLine> bbKlineDataList);

    BBKLine queryOne(Map<String, Object> map);

    void save(BBKLine bbkLine);

    void update(BBKLine bbkLine);

}
