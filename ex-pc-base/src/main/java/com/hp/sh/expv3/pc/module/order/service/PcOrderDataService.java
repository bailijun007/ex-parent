package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderLogDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;

@Service
@Transactional(rollbackFor=Exception.class)
public class PcOrderDataService {

	@Autowired
	private PcOrderDAO pcOrderDAO;

	@Autowired
	private PcOrderLogDAO pcOrderLogDAO;
	
	
	int _________update__________;
	
	
	public void save(PcOrder pcOrder) {
		this.pcOrderDAO.save(pcOrder);
	}

	public long updateStatus(long orderId, long userId, int newStatus, int pendingNew, long now) {
		return this.pcOrderDAO.updateStatus(orderId, userId, newStatus, pendingNew, now);
	}

	public long updateCancelStatus(long orderId, long userId, int pendingCancel, Long now, int canceled, int filled,
			int yes) {
		// TODO Auto-generated method stub
		return 0;
	}

	int _________query__________;

	public BigDecimal getClosingVolume(Long userId, Long id) {
		return null;
	}

	public PcOrder findById(long userId, long orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Long queryCount(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<PcOrder> queryList(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
