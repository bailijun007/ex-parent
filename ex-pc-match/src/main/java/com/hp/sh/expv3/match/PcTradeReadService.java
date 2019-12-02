package com.hp.sh.expv3.match;//package com.hupa.exp.persist.def.pc;


import com.hp.sh.expv3.match.bo.PcTradeBo;

import java.util.List;

public interface PcTradeReadService {

    List<PcTradeBo> listByTakerOrderId(String asset, String symbol, long tkAccountId, long tkOrderId);

    List<PcTradeBo> listByMatchIds(String asset, String symbol, List<Long> matchIds);

}
