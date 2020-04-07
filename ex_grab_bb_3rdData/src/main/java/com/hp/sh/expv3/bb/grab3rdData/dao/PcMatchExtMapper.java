package com.hp.sh.expv3.bb.grab3rdData.dao;

import com.hp.sh.expv3.bb.grab3rdData.pojo.PcMatchExtVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
public interface PcMatchExtMapper {

    void batchSave(@Param("trades") List<PcMatchExtVo> trades, @Param("table") String table);


    void save(@Param("tradeVo") PcMatchExtVo tradeVo);
}
