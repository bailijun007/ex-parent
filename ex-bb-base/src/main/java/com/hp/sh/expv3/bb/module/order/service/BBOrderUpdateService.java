package com.hp.sh.expv3.bb.module.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.constant.BBOrderLogType;
import com.hp.sh.expv3.bb.constant.TriggerType;
import com.hp.sh.expv3.bb.module.order.dao.BBActiveOrderDAO;
import com.hp.sh.expv3.bb.module.order.dao.BBOrderDAO;
import com.hp.sh.expv3.bb.module.order.dao.BBOrderLogDAO;
import com.hp.sh.expv3.bb.module.order.entity.BBActiveOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderLog;
import com.hp.sh.expv3.bb.mq.msg.vo.BBOrderEvent;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.error.ExSysError;
import com.hp.sh.expv3.utils.DbDateUtils;

@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
public class BBOrderUpdateService {
	private static final Logger logger = LoggerFactory.getLogger(BBOrderUpdateService.class);

	@Autowired
	private BBOrderDAO bBOrderDAO;

	@Autowired
	private BBOrderLogDAO bBOrderLogDAO;

	@Autowired
	private BBActiveOrderDAO bBActiveOrderDAO;
	
    @Autowired
    private ApplicationEventPublisher publisher;
	
	public void saveOrder(BBOrder bBOrder) {
		this.bBOrderDAO.save(bBOrder);
		
		this.saveActiveOrder(bBOrder);
		
		//日志
		Long now = DbDateUtils.now();
		this.saveUserOrderLog(bBOrder.getUserId(), bBOrder.getId(), BBOrderLogType.CREATE, now);
	}
	
	public BBOrderLog updateOrder(BBOrder order, long now) {
		this.updateActiveOrder(order);
		
		//日志
		BBOrderLog orderLog = this.saveSysOrderLog(order.getUserId(), order.getId(), BBOrderLogType.SET_STATUS_CANCEL, now);
		this.publishOrderEvent(order, orderLog);
		
		return orderLog;
	}

	public void updateOrder4Trad(BBOrder order){
		this.updateActiveOrder(order);
		
		BBOrderLog orderLog = this.saveSysOrderLog(order.getUserId(), order.getId(), BBOrderLogType.TRADE, order.getModified());
	}

	public BBOrderLog setNewStatus(BBOrder order, long modified) {
		long count = this.bBOrderDAO.updateStatus(OrderStatus.NEW, modified, order.getId(), order.getUserId(), order.getVersion());
		if(count==0){
			throw new ExSysException(ExSysError.UPDATED_ERR, order);
		}
		
		//日志
		BBOrderLog orderLog = this.saveSysOrderLog(order.getUserId(), order.getId(), BBOrderLogType.SET_STATUS_NEW, modified);
		
		//事件
		this.publishOrderEvent(order, orderLog);
		
		return orderLog;
	}

	public void setPendingCancel(int cancelStatus, long modified, long orderId, long userId, Long version) {
		long count = this.bBOrderDAO.updateStatus(cancelStatus, modified, orderId, userId, version);
		
		if(count==0){
			logger.warn("撤单更新失败，orderId={}", orderId);
			return;
		}
		
		//日志
		this.saveUserOrderLog(userId, orderId, BBOrderLogType.CHANGE_STATUS_CANCEL, modified);
	}
	
	private BBOrderLog saveUserOrderLog(long userId, long orderId, int type, long now){
		BBOrderLog bBOrderLog = new BBOrderLog();
		bBOrderLog.setUserId(userId);
		bBOrderLog.setOrderId(orderId);
		bBOrderLog.setTriggerType(TriggerType.USER);
		bBOrderLog.setType(type);
		bBOrderLog.setCreated(now);
		bBOrderLog.setModified(now);
		bBOrderLogDAO.save(bBOrderLog);
		return bBOrderLog;
	}

	private BBOrderLog saveSysOrderLog(long userId, long orderId, int type, long now){
		BBOrderLog bBOrderLog = new BBOrderLog();
		bBOrderLog.setUserId(userId);
		bBOrderLog.setOrderId(orderId);
		bBOrderLog.setTriggerType(TriggerType.SYSTEM);
		bBOrderLog.setType(type);
		bBOrderLog.setCreated(now);
		bBOrderLog.setModified(now);
		bBOrderLogDAO.save(bBOrderLog);
		return bBOrderLog;
	}
	
	private void publishOrderEvent(BBOrder order, BBOrderLog orderLog){
		BBOrderEvent event = new BBOrderEvent(order, orderLog);
		publisher.publishEvent(event);
	}

	private void saveActiveOrder(BBOrder order) {
		BBActiveOrder bBActiveOrder = new BBActiveOrder();
		bBActiveOrder.setId(order.getId());
		bBActiveOrder.setUserId(order.getUserId());
		bBActiveOrder.setAsset(order.getAsset());
		bBActiveOrder.setSymbol(order.getSymbol());
		bBActiveOrder.setBidFlag(order.getBidFlag());
		this.bBActiveOrderDAO.save(bBActiveOrder);
	}

	private void updateActiveOrder(BBOrder order) {
		this.bBOrderDAO.update(order);
		if(order.getActiveFlag()==BBOrder.NO){
			this.bBActiveOrderDAO.delete(order.getId(), order.getUserId());
		}
	}
}
