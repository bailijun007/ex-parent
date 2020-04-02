package com.hp.sh.expv3.pc.trade.service;

import com.hp.sh.expv3.pc.trade.pojo.BbMatchExtVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
public interface BbMatchExtService {
    void batchSave(List<BbMatchExtVo> trades, String table);
}
