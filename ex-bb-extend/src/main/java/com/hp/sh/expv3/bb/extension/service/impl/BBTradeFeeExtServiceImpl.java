package com.hp.sh.expv3.bb.extension.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.extension.dao.BBTradeFeeExtMapper;
import com.hp.sh.expv3.bb.extension.service.BBTradeFeeExtService;

@Service
@Transactional(readOnly=true)
public class BBTradeFeeExtServiceImpl implements BBTradeFeeExtService {
	
	@Autowired
	private BBTradeFeeExtMapper tradeFeeExtDAO;

	@Override
	public BigDecimal query(Long userId, String asset, Integer makerFlag, Long beginTime, Long endTime) {
		BigDecimal n = tradeFeeExtDAO.query(userId, asset, makerFlag, beginTime, endTime);
		return n;
	}

}
