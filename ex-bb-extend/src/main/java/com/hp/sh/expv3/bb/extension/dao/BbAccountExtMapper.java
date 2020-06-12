package com.hp.sh.expv3.bb.extension.dao;

import com.hp.sh.expv3.bb.extension.vo.BbAccountExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/2/13
 */
public interface BbAccountExtMapper {

    int save(BbAccountVo bbAccountVo);

    BbAccountVo queryOne(Map<String,Object> map);

    List<BbAccountVo> queryList(Map<String,Object> map);

    BbAccountExtVo getBBAccount(@Param("userId") Long userId, @Param("asset") String asset);

    BigDecimal queryTotalNumber( @Param("asset") String asset,@Param("modifiedBegin") Long modifiedBegin,@Param("modifiedEnd")  Long modifiedEnd);
}
