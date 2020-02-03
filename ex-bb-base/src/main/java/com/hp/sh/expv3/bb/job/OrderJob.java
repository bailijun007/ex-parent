package com.hp.sh.expv3.bb.job;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.module.order.entity.PcOrder;
import com.hp.sh.expv3.bb.module.order.service.PcOrderQueryService;
import com.hp.sh.expv3.bb.module.order.service.PcOrderService;
import com.hp.sh.expv3.bb.mq.MatchMqSender;
import com.hp.sh.expv3.utils.DbDateUtils;

//@Component
public class OrderJob {
    private static final Logger logger = LoggerFactory.getLogger(OrderJob.class);

    @Autowired
    private PcOrderService orderService;
	@Autowired
	private PcOrderQueryService orderQueryService;

	@Autowired
	private MatchMqSender matchMqSender;
	
//	@Scheduled(cron = "0/10 * * * * ?")
	public void handle() {
		Long now = DbDateUtils.now();
		Page page = new Page(1, 100, 1000L);
		while(true){
			List<PcOrder> list = this.orderQueryService.pageQuery(page, OrderStatus.PENDING_NEW, System.currentTimeMillis()-1000*30);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(PcOrder order : list){
				matchMqSender.sendPendingNew(order);
			}
			
			page.setPageNo(page.getPageNo()+1);
		}
	}




}
