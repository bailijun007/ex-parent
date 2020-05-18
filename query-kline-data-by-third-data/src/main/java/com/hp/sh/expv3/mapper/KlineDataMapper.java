package com.hp.sh.expv3.mapper;


import com.hp.sh.expv3.pojo.KlineDataPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/5/6
 */
@Mapper
public interface KlineDataMapper {
    List<KlineDataPo> queryKlineDataByThirdData(@Param("tableName") String tableName, @Param("klineType") Integer klineType, @Param("pair") String pair, @Param("interval") String interval, @Param("openTimeBegin") Long openTimeBegin, @Param("openTimeEnd") Long openTimeEnd, @Param("expName") String expName);

}
