package com.hp.sh.expv3.pc.extension.dao;

import com.hp.sh.expv3.pc.extension.vo.PcAccountLogVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/25
 */
public interface PcAccountLogDAO {

    int save(PcAccountLogVo pcAccountLogVo);

    PcAccountLogVo  queryOne(Map<String,Object> map);

    List<PcAccountLogVo> queryList(Map<String,Object> map);

    Long queryCount(Map<String, Object> map);

    List<PcAccountLogVo> queryByLimit(Map<String, Object> map);


    List<PcAccountLogVo> queryByNextPage(Map<String, Object> map);
}
