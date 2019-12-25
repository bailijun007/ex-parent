package com.hp.sh.expv3.pc.extension.dao;

import com.hp.sh.expv3.pc.extension.vo.PcAccountSymbolVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/21
 */
public interface PcAccountSymbolDAO {

    PcAccountSymbolVo queryOne(Map<String,Object> map);

    List<PcAccountSymbolVo> queryList(Map<String,Object> map);

}
