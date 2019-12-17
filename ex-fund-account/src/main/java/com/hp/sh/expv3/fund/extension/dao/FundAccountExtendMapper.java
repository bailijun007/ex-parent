package com.hp.sh.expv3.fund.extension.dao;

import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiLiJun  on 2019/12/13
 */
public interface FundAccountExtendMapper {

    CapitalAccountVo getCapitalAccount(@Param("userId") Long userId, @Param("asset")String asset);
}