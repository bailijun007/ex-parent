package com.hp.sh.expv3.bb.extension.service;

import java.math.BigDecimal;

public interface BBTradeFeeExtService {

	BigDecimal query(Long userId, String asset, Integer makerFlag, Long beginTime, Long endTime);

}
