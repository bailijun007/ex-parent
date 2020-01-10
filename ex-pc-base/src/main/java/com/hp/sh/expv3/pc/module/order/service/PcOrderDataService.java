package com.hp.sh.expv3.pc.module.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderLogDAO;

@Service
@Transactional(rollbackFor=Exception.class)
public class PcOrderDataService {

	@Autowired
	private PcOrderDAO pcOrderDAO;

	@Autowired
	private PcOrderLogDAO pcOrderLogDAO;
	
}
