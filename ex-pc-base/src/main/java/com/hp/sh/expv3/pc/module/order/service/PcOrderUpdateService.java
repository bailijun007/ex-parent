package com.hp.sh.expv3.pc.module.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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

@Service
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
		//日志
		Long now = DbDateUtils.now();
		this.saveUserOrderLog(pcOrder.getUserId(), pcOrder.getId(), PcOrderLogType.CREATE, now);
		
		this.saveActiveOrder(pcOrder);
	}
	
	public PcOrderLog updateOrder(PcOrder order, long now) {
		this.pcOrderDAO.update(order);
		
		//日志
		PcOrderLog orderLog = this.saveSysOrderLog(order.getUserId(), order.getId(), PcOrderLogType.SET_STATUS_CANCEL, now);
		
		this.publishOrderEvent(order, orderLog);
		
		this.updateActiveOrder(order);
		
		return orderLog;
	}

	public void updateOrder4Trad(PcOrder order){
		this.pcOrderDAO.update(order);
		this.saveSysOrderLog(order.getUserId(), order.getId(), PcOrderLogType.TRADE, order.getModified());
		
		this.updateActiveOrder(order);
	}

	public PcOrderLog setNewStatus(long orderId, long userId, int newStatus, int pendingNew, long modified) {
		long count = this.pcOrderDAO.updateStatus(orderId, userId, OrderStatus.NEW, OrderStatus.PENDING_NEW, modified);
		if(count==0){
			logger.error("更新失败，orderId={}", orderId);
			return null;
		}
		//日志
		PcOrderLog orderLog = this.saveSysOrderLog(userId, orderId, PcOrderLogType.SET_STATUS_NEW, modified);
		
		//事件
		PcOrder pcOrder = this.pcOrderDAO.findById(userId, orderId);
		this.publishOrderEvent(pcOrder, orderLog);
		
		return orderLog;
	}

	public void setUserCancelStatus(long orderId, long userId, int cancelStatus, long modified, int status1, int status2, int activeFlag) {
		long count = this.pcOrderDAO.updateCancelStatus(orderId, userId, cancelStatus, modified, status1, status2, activeFlag);
		
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
