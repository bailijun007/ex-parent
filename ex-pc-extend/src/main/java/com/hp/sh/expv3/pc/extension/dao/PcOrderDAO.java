package com.hp.sh.expv3.pc.extension.dao;

import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface PcOrderDAO {

    BigDecimal getGrossMargin(@Param("userId") Long userId,@Param("asset") String asset);

    PcOrder queryOne( Map<String,Object> map);

}
