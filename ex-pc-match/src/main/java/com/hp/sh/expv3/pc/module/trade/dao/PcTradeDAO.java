package com.hp.sh.expv3.pc.module.trade.dao;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.pc.module.trade.entity.PcTrade;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PcTradeDAO extends BaseMapper<PcTrade, Long> {

    List<PcTrade> queryList(Map<String, Object> params);

    PcTrade queryOne(Map<String, Object> params);

    Long queryCount(Map<String, Object> params);

    <T extends PcTrade> void batchSave(List<T> trades);

}