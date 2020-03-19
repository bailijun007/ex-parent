package com.hp.sh.expv3.pc.module.liq.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.pc.module.liq.dao.PcLiqRecordDAO;
import com.hp.sh.expv3.pc.module.liq.entity.PcLiqRecord;

/**
 * 强平记录
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcLiqRecordService {
	private static final Logger logger = LoggerFactory.getLogger(PcLiqRecordService.class);
    
    @Autowired
    private PcLiqRecordDAO liqRecordDAO;
    
    @Autowired
    private ApplicationEventPublisher publisher;

	public List<PcLiqRecord> queryPending(Page page, Long userId, Long startTime, Long startId) {
		List<PcLiqRecord> list = liqRecordDAO.queryPending(page, userId, startTime, startId);
		return list;
	}


    

}
