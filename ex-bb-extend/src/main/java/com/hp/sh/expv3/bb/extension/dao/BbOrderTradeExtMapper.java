package com.hp.sh.expv3.bb.extension.dao;

import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/2/15
 */
public interface BbOrderTradeExtMapper {
    BbOrderTradeVo queryOne(Map<String,Object> map);

    List<BbOrderTradeVo> queryList(Map<String,Object> map);

    Long queryCount();
}
