package com.hp.sh.expv3.bb.extension.dao;

import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/2/14
 */
public interface BbAccountRecordExtMapper {

    BbAccountRecordVo  queryOne(Map<String,Object> map);

   List<BbAccountRecordVo> queryList(Map<String,Object> map);
}
