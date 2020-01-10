/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.match.core.match.handler.PcLimitOrderHandler;
import com.hp.sh.expv3.match.match.core.match.task.service.PcOrderBookEventService;
import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.matched.task.def.PcMatchedTaskService;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.PriorityQueue;

@Service
@Scope("prototype")
public class PcOrderCancelTask extends PcOrderBaseTask implements ApplicationContextAware {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PcLimitOrderHandler pcLimitOrderHandler;

    private long orderId;
    private long accountId;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    @Override
    public void onSucess() {

    }

    @Override
    public boolean onError(Exception ex) {
        return false;
    }

    @Autowired
    @Qualifier("threadManagerPcMatchImpl")
    private IThreadManager threadManagerMatchImpl;

    @Autowired
    private PcOrderBookEventService pcOrderBookEventService;
    @Autowired
    private PcMatchedTaskService pcMatchedTaskService;

    @Override
    public void run() {

        PcMatchHandlerContext context = PcMatchHandlerContext.getLocalContext();

        PcOrder4MatchBo order = context.allOpenOrders.get(orderId);
        if (null == order) {
            if (this.getCurrentMsgOffset() > context.getSentMqOffset()) {
                pcMatchedTaskService.addMatchedOrderCancelTask(context, this.getCurrentMsgOffset(), this.getCurrentMsgId(), accountId, orderId, null);
            }
        } else {
            PriorityQueue<PcOrder4MatchBo> queue = context.getQueue(order.getBidFlag());
            queue.remove(order);
            context.allOpenOrders.remove(orderId);
            if (this.getCurrentMsgOffset() > context.getSentMqOffset()) {
                pcMatchedTaskService.addMatchedOrderCancelTask(context, this.getCurrentMsgOffset(), this.getCurrentMsgId(), accountId, orderId, order.getNumber().subtract(order.getFilledNumber()));
            }
        }
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}