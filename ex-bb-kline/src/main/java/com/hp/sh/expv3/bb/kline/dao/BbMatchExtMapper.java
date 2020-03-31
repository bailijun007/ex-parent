package com.hp.sh.expv3.bb.kline.dao;

import com.hp.sh.expv3.bb.kline.pojo.BbMatchExtVo;
import com.hp.sh.expv3.bb.kline.vo.BbRepairTradeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
public interface BbMatchExtMapper {

    void batchSave(@Param("trades") List<BbMatchExtVo> trades);
}
