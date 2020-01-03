package com.hp.sh.expv3.pc.extension.dao;

import com.hp.sh.expv3.pc.extension.vo.PcTradeVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface PcTradeDAO {

    PcTradeVo queryOne(Map<String,Object> map);

    List<PcTradeVo> queryList(Map<String,Object> map);

    List<PcTradeVo> queryTradeByGtTime(Map<String, Object> map);
}
