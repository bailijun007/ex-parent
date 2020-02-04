package com.hp.sh.expv3.bb.module.position.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.module.order.entity.BBActiveOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.position.dao.BBActivePositionDAO;
import com.hp.sh.expv3.bb.module.position.dao.BBPositionDAO;
import com.hp.sh.expv3.bb.module.position.entity.BBActivePosition;
import com.hp.sh.expv3.bb.module.position.entity.BBPosition;
import com.hp.sh.expv3.bb.module.position.vo.PosUID;
import com.hp.sh.expv3.utils.math.BigUtils;

@Service
@Transactional(rollbackFor=Exception.class)
public class BBPositionDataService {
	private static final Logger logger = LoggerFactory.getLogger(BBPositionDataService.class);

	@Autowired
	private BBPositionDAO bBPositionDAO;
	
	@Autowired
	private BBActivePositionDAO bBActivePositionDAO;
	
	@Autowired
    private ApplicationEventPublisher publisher;
	
	public void save(BBPosition bBPosition) {
		this.bBPositionDAO.save(bBPosition);
		this.saveActivePos(bBPosition);
	}

	public void update(BBPosition bBPosition){
		this.bBPositionDAO.update(bBPosition);
		this.updateActivePos(bBPosition);
		publisher.publishEvent(bBPosition);
	}

	/**
	 * 获取当前仓位，如果没有则创建一个
	 * @param userId
	 * @param asset
	 * @param symbol
	 * @param longFlag 多/空
	 * @return
	 */
	public BBPosition getCurrentPosition(Long userId, String asset, String symbol, int longFlag){
		BBPosition pos = this.bBPositionDAO.getActivePos(userId, asset, symbol, longFlag);
		return pos;
	}

	public BBPosition getPosition(long userId, Long id) {
		return this.bBPositionDAO.findById(userId, id);
	}

	public BBPosition getPosition(long userId, String asset, String symbol, Long id) {
		return this.bBPositionDAO.findById(userId, id);
	}

	public List<BBPosition> queryActivePosList(Page page, Long userId, String asset, String symbol) {
		List<BBPosition> list = this.bBPositionDAO.queryActivePosList(page, userId, asset, symbol);
		return list;
	}

	public List<PosUID> queryActivePosIdList(Page page, Long userId, String asset, String symbol) {
		List<PosUID> list = this.bBPositionDAO.queryActivePosIdList(page, userId, asset, symbol);
		return list;
	}

	private void saveActivePos(BBPosition bBPosition) {
		BBActivePosition bBActivePosition = new BBActivePosition();
		bBActivePosition.setId(bBPosition.getId());
		bBActivePosition.setUserId(bBPosition.getUserId());
		bBActivePosition.setAsset(bBPosition.getAsset());
		bBActivePosition.setSymbol(bBPosition.getSymbol());
		this.bBActivePositionDAO.save(bBActivePosition);
	}

	private void updateActivePos(BBPosition bBPosition) {
		if(BigUtils.isZero(bBPosition.getVolume())){
			this.bBActivePositionDAO.delete(bBPosition.getId(), bBPosition.getUserId());
		}
	}
	
}
