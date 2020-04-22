package com.hp.sh.expv3.bb.trade.service;

import com.hp.sh.expv3.bb.trade.pojo.BbMatchExtVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
public interface BbMatchExtService {
    int batchSave(List<BbMatchExtVo> trades, String table);
}
