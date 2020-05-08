package com.hp.sh.expv3.bb.extension.thirdKlineData.mapper;


import com.hp.sh.expv3.bb.extension.vo.KlineDataPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
 * @author BaiLiJun  on 2020/5/6
 */
@Mapper
public interface ThirdKlineDataMapper {
    List<KlineDataPo> queryKlineDataByThirdData(@Param("tableName") String tableName, @Param("klineType") Integer klineType, @Param("pair") String pair, @Param("interval") String interval, @Param("openTimeBegin") Long openTimeBegin, @Param("openTimeEnd") Long openTimeEnd, @Param("expName") String expName);

    Integer existTable(@Param("dbName") String dbName,@Param("tableName") String tableName);
}
