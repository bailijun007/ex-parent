package com.hp.sh.expv3.pc.module.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.pc.constant.OrderStatus;
import com.hp.sh.expv3.pc.constant.PcOrderLogType;
import com.hp.sh.expv3.pc.constant.TriggerType;
import com.hp.sh.expv3.pc.module.order.dao.PcActiveOrderDAO;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcActiveOrder;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.mq.extend.msg.PcOrderEvent;
import com.hp.sh.expv3.utils.DbDateUtils;

@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
public class PcOrderUpdateService {
	private static final Logger logger = LoggerFactory.getLogger(PcOrderUpdateService.class);

	@Autowired
	private PcOrderDAO pcOrderDAO;

	@Autowired
	private PcActiveOrderDAO pcActiveOrderDAO;
	
    @Autowired
    private ApplicationEventPublisher publisher;
	
	public void createNewOrder(PcOrder pcOrder) {
		this.pcOrderDAO.save(pcOrder);
		this.saveActiveOrder(pcOrder);
		
		//日志
		Long now = DbDateUtils.now();
	}
	
	public void setNewStatus(PcOrder order, long modified) {
		long count = this.pcOrderDAO.updateStatus(OrderStatus.NEW, modified, order.getId(), order.getUserId(), order.getVersion());
		if(count==0){
			throw new UpdateException("更新失败", order);
		}
		
		//事件
		this.publishOrderEvent(order);
	}

	public void setPendingCancelStatus(long modified, long orderId, long userId, long version) {
		long count = this.pcOrderDAO.updateStatus(OrderStatus.PENDING_CANCEL, modified, orderId, userId, version);
		
		if(count==0){
			logger.warn("撤单更新失败，orderId={}", orderId);
			return;
		}
	}

	public void setCancelStatus(PcOrder order, long now) {
		this.pcOrderDAO.update(order);
		
		this.updateActiveOrder(order);
		
		//日志
		this.publishOrderEvent(order);
	}

	public void updateOrder4Trad(PcOrder order){
		this.pcOrderDAO.update(order);
		
		this.updateActiveOrder(order);
	}
	
	private void publishOrderEvent(PcOrder order){
		PcOrderEvent event = new PcOrderEvent(order, null);
		publisher.publishEvent(event);
	}

	private void saveActiveOrder(PcOrder pcOrder) {
		PcActiveOrder pcActiveOrder = new PcActiveOrder();
		pcActiveOrder.setId(pcOrder.getId());
		pcActiveOrder.setUserId(pcOrder.getUserId());
		pcActiveOrder.setAsset(pcOrder.getAsset());
		pcActiveOrder.setSymbol(pcOrder.getSymbol());
		pcActiveOrder.setLongFlag(pcOrder.getLongFlag());
		this.pcActiveOrderDAO.save(pcActiveOrder);
	}

	private void updateActiveOrder(PcOrder order) {
		if(order.getActiveFlag()==PcOrder.NO){
			this.pcActiveOrderDAO.delete(order.getId(), order.getUserId());
		}
	}
}
