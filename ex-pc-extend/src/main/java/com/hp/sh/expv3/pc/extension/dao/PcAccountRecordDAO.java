package com.hp.sh.expv3.pc.extension.dao;

import com.hp.sh.expv3.pc.extension.vo.PcAccountRecordVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/21
 */
public interface PcAccountRecordDAO {

    PcAccountRecordVo queryOne(Map<String,Object> map);

    List<PcAccountRecordVo> queryList(Map<String,Object> map);

}
