package com.hp.sh.expv3.bb.module.trade.dao;

import com.hp.sh.expv3.bb.module.trade.entity.BbMatch;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BbMatchDAO {

    List<BbMatch> queryList(Map<String, Object> params);

    <T extends BbMatch> void batchSave(List<T> trades);

}