package com.hp.sh.expv3.bb.grab3rdData.service;

import com.hp.sh.expv3.bb.grab3rdData.pojo.PcMatchExtVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
public interface PcMatchExtService {
    void batchSave(List<PcMatchExtVo> trades, String table);
}
