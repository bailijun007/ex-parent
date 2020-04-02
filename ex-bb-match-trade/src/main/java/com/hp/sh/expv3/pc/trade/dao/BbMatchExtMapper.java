package com.hp.sh.expv3.pc.trade.dao;

import com.hp.sh.expv3.pc.trade.pojo.BbMatchExtVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
@Mapper
public interface BbMatchExtMapper {

    void batchSave(@Param("trades") List<BbMatchExtVo> trades, @Param("table")String table);


    void save(@Param("tradeVo") BbMatchExtVo tradeVo);
}