package com.hp.sh.expv3.bb.extension.dao;

import com.hp.sh.expv3.bb.extension.vo.BbAccountVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/2/13
 */
public interface BbAccountExtMapper {

    int save(BbAccountVo bbAccountVo);

    BbAccountVo queryOne(Map<String,Object> map);

    List<BbAccountVo> queryList(Map<String,Object> map);

}
