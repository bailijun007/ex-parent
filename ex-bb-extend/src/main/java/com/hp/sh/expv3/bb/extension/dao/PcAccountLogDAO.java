package com.hp.sh.expv3.bb.extension.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.bb.extension.vo.PcAccountLogVo;

/**
 * @author BaiLiJun  on 2019/12/25
 */
public interface PcAccountLogDAO {

    int save(PcAccountLogVo pcAccountLogVo);

    PcAccountLogVo  queryOne(Map<String,Object> map);

    List<PcAccountLogVo> queryList(Map<String,Object> map);

    Long queryCount(Map<String, Object> map);

    List<PcAccountLogVo> queryByLimit(Map<String, Object> map);


}
