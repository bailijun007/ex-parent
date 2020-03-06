package com.hp.sh.expv3.pc.module.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.error.ExSysError;
import com.hp.sh.expv3.pc.constant.OrderStatus;
import com.hp.sh.expv3.pc.constant.PcOrderLogType;
import com.hp.sh.expv3.pc.constant.TriggerType;
import com.hp.sh.expv3.pc.module.order.dao.PcActiveOrderDAO;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderLogDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcActiveOrder;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderLog;
import com.hp.sh.expv3.pc.mq.extend.msg.PcOrderEvent;
import com.hp.sh.expv3.utils.DbDateUtils;

@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
public class PcOrderUpdateService {
	private static final Logger logger = LoggerFactory.getLogger(PcOrderUpdateService.class);

	@Autowired
	private PcOrderDAO pcOrderDAO;

	@Autowired
	private PcOrderLogDAO pcOrderLogDAO;

	@Autowired
	private PcActiveOrderDAO pcActiveOrderDAO;
	
    @Autowired
    private ApplicationEventPublisher publisher;
	
	public void saveOrder(PcOrder pcOrder) {
		this.pcOrderDAO.save(pcOrder);
		this.saveActiveOrder(pcOrder);
		
		//日志
		Long now = DbDateUtils.now();
		this.saveUserOrderLog(pcOrder.getUserId(), pcOrder.getId(), PcOrderLogType.CREATE, now);
	}
	
	public PcOrderLog updateOrder(PcOrder order, long now) {
		this.pcOrderDAO.update(order);
		
		this.updateActiveOrder(order);
		
		//日志
		PcOrderLog orderLog = this.saveSysOrderLog(order.getUserId(), order.getId(), PcOrderLogType.SET_STATUS_CANCEL, now);
		this.publishOrderEvent(order, orderLog);
		
		return orderLog;
	}

	public void updateOrder4Trad(PcOrder order){
		this.pcOrderDAO.update(order);
		
		this.updateActiveOrder(order);
		
		this.saveSysOrderLog(order.getUserId(), order.getId(), PcOrderLogType.TRADE, order.getModified());
	}

	public PcOrderLog setNewStatus(PcOrder order, long modified) {
		long count = this.pcOrderDAO.updateStatus(OrderStatus.NEW, modified, order.getId(), order.getUserId(), order.getVersion());
		if(count==0){
			throw new ExSysException(ExSysError.UPDATED_ERR, order);
		}
		
		//日志
		PcOrderLog orderLog = this.saveSysOrderLog(order.getUserId(), order.getId(), PcOrderLogType.SET_STATUS_NEW, modified);
		
		//事件
		this.publishOrderEvent(order, orderLog);
		
		return orderLog;
	}

	public void setPendingCancelStatus(long modified, long orderId, long userId, long version) {
		long count = this.pcOrderDAO.updateStatus(OrderStatus.PENDING_CANCEL, modified, orderId, userId, version);
		
		if(count==0){
			logger.error("撤单更新失败，orderId={}", orderId);
			return;
		}
		
		//日志
		this.saveUserOrderLog(userId, orderId, PcOrderLogType.CHANGE_STATUS_CANCEL, modified);
	}
	
	private PcOrderLog saveUserOrderLog(long userId, long orderId, int type, long now){
		PcOrderLog pcOrderLog = new PcOrderLog();
		pcOrderLog.setUserId(userId);
		pcOrderLog.setOrderId(orderId);
		pcOrderLog.setTriggerType(TriggerType.USER);
		pcOrderLog.setType(type);
		pcOrderLog.setCreated(now);
		pcOrderLog.setModified(now);
		pcOrderLogDAO.save(pcOrderLog);
		return pcOrderLog;
	}

	private PcOrderLog saveSysOrderLog(long userId, long orderId, int type, long now){
		PcOrderLog pcOrderLog = new PcOrderLog();
		pcOrderLog.setUserId(userId);
		pcOrderLog.setOrderId(orderId);
		pcOrderLog.setTriggerType(TriggerType.SYSTEM);
		pcOrderLog.setType(type);
		pcOrderLog.setCreated(now);
		pcOrderLog.setModified(now);
		pcOrderLogDAO.save(pcOrderLog);
		return pcOrderLog;
	}
	
	private void publishOrderEvent(PcOrder order, PcOrderLog pcOrderLog){
		PcOrderEvent event = new PcOrderEvent(order, pcOrderLog);
		publisher.publishEvent(event);
	}

	private void saveActiveOrder(PcOrder pcOrder) {
		PcActiveOrder pcActiveOrder = new PcActiveOrder();
		pcActiveOrder.setId(pcOrder.getId());
		pcActiveOrder.setUserId(pcOrder.getUserId());
		pcActiveOrder.setAsset(pcOrder.getAsset());
		pcActiveOrder.setSymbol(pcOrder.getSymbol());
		this.pcActiveOrderDAO.save(pcActiveOrder);
	}

	private void updateActiveOrder(PcOrder order) {
		if(order.getActiveFlag()==PcOrder.NO){
			this.pcActiveOrderDAO.delete(order.getId(), order.getUserId());
		}
	}
}
