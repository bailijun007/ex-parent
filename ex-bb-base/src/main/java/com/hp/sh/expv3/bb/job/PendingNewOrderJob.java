package com.hp.sh.expv3.bb.job;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.service.BBOrderQueryService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.mq.send.MatchMqSender;
import com.hp.sh.expv3.utils.DbDateUtils;

//@Component
public class PendingNewOrderJob {
    private static final Logger logger = LoggerFactory.getLogger(PendingNewOrderJob.class);

    @Autowired
    private BBOrderService orderService;
	@Autowired
	private BBOrderQueryService orderQueryService;

	@Autowired
	private MatchMqSender matchMqSender;
	
//	@Scheduled(cron = "0/10 * * * * ?")
	public void handle() {
		Long now = DbDateUtils.now();
		Page page = new Page(1, 100, 1000L);
		while(true){
			List<BBOrder> list = this.orderQueryService.queryPendingActive(page, null, System.currentTimeMillis()-1000*30, OrderStatus.PENDING_NEW);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(BBOrder order : list){
				matchMqSender.sendPendingNew(order);
			}
			
			page.setPageNo(page.getPageNo()+1);
		}
	}




}
