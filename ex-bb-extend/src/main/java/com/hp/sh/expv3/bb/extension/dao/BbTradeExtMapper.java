package com.hp.sh.expv3.bb.extension.dao;

import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/2/15
 */
public interface BbTradeExtMapper {
    BbTradeVo queryOne(Map<String,Object> map);

    List<BbTradeVo> queryList(Map<String,Object> map);
}
