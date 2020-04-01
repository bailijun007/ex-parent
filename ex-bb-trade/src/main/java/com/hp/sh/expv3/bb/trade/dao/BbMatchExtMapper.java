package com.hp.sh.expv3.bb.trade.dao;

import com.hp.sh.expv3.bb.trade.pojo.BbMatchExtVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
@Mapper
public interface BbMatchExtMapper {

    void batchSave(@Param("trades") List<BbMatchExtVo> trades);
}
