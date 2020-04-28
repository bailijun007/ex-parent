package com.hp.sh.expv3.pc.trade.service;



import com.hp.sh.expv3.pc.trade.pojo.PcMatchExtVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
public interface PcMatchExtService {
    int batchSave(List<PcMatchExtVo> trades, String table);
}
