package com.hp.sh.expv3.pc.module.position.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;

@Service
@Transactional
public class PcPositionService {

	@Autowired
	private PcPositionDAO pcPositionDAO;
	
	
}
