package com.hp.sh.expv3.pc.module.position.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionHistoryDAO;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.entity.PcPositionHistory;
import com.hp.sh.expv3.pc.module.position.vo.PosUID;
import com.hp.sh.expv3.utils.math.BigUtils;

@Service
@Transactional(rollbackFor=Exception.class)
public class PcPositionDataService {
	private static final Logger logger = LoggerFactory.getLogger(PcPositionDataService.class);

	@Autowired
	private PcPositionDAO pcPositionDAO;
	
	@Autowired
	private PcPositionHistoryDAO pcPositionHistoryDAO;
	
	@Autowired
    private ApplicationEventPublisher publisher;
	
	/**
	 * 查询减仓仓位
	 */
	@CrossDB
	public PcPosition getCutPos(String asset, String symbol, int longFlag) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("asset", asset);
		params.put("symbol", symbol);
		
		PcPosition pos = this.pcPositionDAO.getCutPos(asset, symbol, longFlag);
		return pos;
	}
	
	public void save(PcPosition pcPosition) {
		this.pcPositionDAO.save(pcPosition);
	}

	public void update(PcPosition pcPosition){
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

	public List<PosUID> queryActivePosIdList(Page page, Long userId, String asset, String symbol, Long startId) {
		List<PosUID> list = this.pcPositionDAO.queryActivePosIdList(page, userId, asset, symbol, startId);
		return list;
	}

	private void updateActivePos(PcPosition pcPosition) {
		if(BigUtils.gtZero(pcPosition.getVolume())){
			this.pcPositionDAO.update(pcPosition);
		}else{
			this.pcPositionDAO.delete(pcPosition.getId(), pcPosition.getUserId(), pcPosition.getAsset(), pcPosition.getSymbol());
			//保存到历史订单
			PcPositionHistory positionHistory = BeanHelper.copyBean(pcPosition, PcPositionHistory.class);
			pcPositionHistoryDAO.save(positionHistory);
		}
	}
	
}
