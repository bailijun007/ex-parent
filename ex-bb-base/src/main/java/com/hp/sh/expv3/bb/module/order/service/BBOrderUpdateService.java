package com.hp.sh.expv3.bb.module.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.module.order.dao.BBOrderDAO;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderLog;
import com.hp.sh.expv3.bb.mq.msg.vo.BBOrderEvent;

@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
public class BBOrderUpdateService {
	private static final Logger logger = LoggerFactory.getLogger(BBOrderUpdateService.class);

	@Autowired
	private BBOrderDAO orderDAO;

    @Autowired
    private ApplicationEventPublisher publisher;
    
    @Autowired
    private BBOrderUpdateService self;
	
	public void saveOrder(BBOrder bBOrder) {
		self.saveActiveOrder(bBOrder);
	}
	
	public void updateOrder(BBOrder order, long now) {
		self.updateActiveOrder(order);
		this.publishOrderEvent(order, null);
	}

	public void updateOrder4Trad(BBOrder order){
		self.updateActiveOrder(order);
	}

	public void setNewStatus(BBOrder order, long modified) {
		long count = this.orderDAO.updateStatus(OrderStatus.NEW, modified, order.getId(), order.getUserId(), order.getVersion());
		if(count==0){
			throw new UpdateException("更新失败", order);
		}
		
		//事件
		this.publishOrderEvent(order, null);
		
	}

	public void setPendingCancel(int cancelStatus, long modified, long orderId, long userId, Long version) {
		long count = this.orderDAO.updateStatus(cancelStatus, modified, orderId, userId, version);
		
		if(count==0){
			logger.warn("撤单更新失败，orderId={}", orderId);
			return;
		}
	}

	private void publishOrderEvent(BBOrder order, BBOrderLog orderLog){
		BBOrderEvent event = new BBOrderEvent(order, orderLog);
		publisher.publishEvent(event);
	}

	void saveActiveOrder(BBOrder order) {
		this.orderDAO.save(order);
	}

	void updateActiveOrder(BBOrder order) {
		this.orderDAO.update(order);
		if(order.getActiveFlag()==BBOrder.NO){
			
		}
	}
}
