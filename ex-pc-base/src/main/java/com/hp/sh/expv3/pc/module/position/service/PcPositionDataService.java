package com.hp.sh.expv3.pc.module.position.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.pc.module.order.entity.PcActiveOrder;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.position.dao.PcActivePositionDAO;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.module.position.entity.PcActivePosition;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.vo.PosUID;
import com.hp.sh.expv3.utils.math.BigUtils;

@Service
@Transactional(rollbackFor=Exception.class)
public class PcPositionDataService {
	private static final Logger logger = LoggerFactory.getLogger(PcPositionDataService.class);

	@Autowired
	private PcPositionDAO pcPositionDAO;
	
	@Autowired
	private PcActivePositionDAO pcActivePositionDAO;
	
	@Autowired
    private ApplicationEventPublisher publisher;
	
	public void save(PcPosition pcPosition) {
		this.pcPositionDAO.save(pcPosition);
		this.saveActivePos(pcPosition);
	}

	public void update(PcPosition pcPosition){
		this.pcPositionDAO.update(pcPosition);
		this.updateActivePos(pcPosition);
		publisher.publishEvent(pcPosition);
	}

	/**
	 * 获取当前仓位，如果没有则创建一个
	 * @param userId
	 * @param asset
	 * @param symbol
	 * @param longFlag 多/空
	 * @return
	 */
	public PcPosition getCurrentPosition(Long userId, String asset, String symbol, int longFlag){
		PcPosition pos = this.pcPositionDAO.getActivePos(userId, asset, symbol, longFlag);
		return pos;
	}

	public PcPosition getPosition(long userId, Long id) {
		return this.pcPositionDAO.findById(userId, id);
	}

	public PcPosition getPosition(long userId, String asset, String symbol, Long id) {
		return this.pcPositionDAO.findById(userId, id);
	}

	public List<PcPosition> queryActivePosList(Page page, Long userId, String asset, String symbol) {
		List<PcPosition> list = this.pcPositionDAO.queryActivePosList(page, userId, asset, symbol);
		return list;
	}

	public List<PosUID> queryActivePosIdList(Page page, Long userId, String asset, String symbol) {
		List<PosUID> list = this.pcPositionDAO.queryActivePosIdList(page, userId, asset, symbol);
		return list;
	}

	private void saveActivePos(PcPosition pcPosition) {
		PcActivePosition pcActivePosition = new PcActivePosition();
		pcActivePosition.setId(pcPosition.getId());
		pcActivePosition.setUserId(pcPosition.getUserId());
		pcActivePosition.setAsset(pcPosition.getAsset());
		pcActivePosition.setSymbol(pcPosition.getSymbol());
		this.pcActivePositionDAO.save(pcActivePosition);
	}

	private void updateActivePos(PcPosition pcPosition) {
		if(BigUtils.isZero(pcPosition.getVolume())){
			this.pcActivePositionDAO.delete(pcPosition.getId(), pcPosition.getUserId());
		}
	}
	
}
