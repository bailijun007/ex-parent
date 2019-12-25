package com.hp.sh.expv3.pc.extension.dao;

import com.hp.sh.expv3.pc.extension.vo.PcLiqRecordVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/25
 */
public interface PcLiqRecordDAO {

    PcLiqRecordVo queryOne(Map<String,Object> map);

    List<PcLiqRecordVo> queryList(Map<String,Object> map);

}
