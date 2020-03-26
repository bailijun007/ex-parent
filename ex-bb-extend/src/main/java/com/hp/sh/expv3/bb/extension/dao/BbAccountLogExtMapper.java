package com.hp.sh.expv3.bb.extension.dao;

import com.hp.sh.expv3.bb.extension.vo.BbAccountLogExtVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/3/24
 */
public interface BbAccountLogExtMapper {


    List<BbAccountLogExtVo> queryByLimit(Map<String, Object> map);

    List<BbAccountLogExtVo> listBbAccountLogsByPage(Map<String, Object> map);
}
