/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.match.core.match.handler.BbLimitOrderHandler;
import com.hp.sh.expv3.match.match.core.match.task.service.BbOrderBookEventService;
import com.hp.sh.expv3.match.match.core.match.thread.BbMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.matched.task.def.BbMatchedTaskService;
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
public class BbOrderCancelTask extends BbOrderBaseTask implements ApplicationContextAware {
    final Logger logger = LoggerFactory.getLogger(getClass());

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
    @Qualifier("threadManagerBbMatchImpl")
    private IThreadManager threadManagerMatchImpl;

    @Autowired
    private BbOrderBookEventService bbOrderBookEventService;
    @Autowired
    private BbMatchedTaskService bbMatchedTaskService;

    @Override
    public void run() {

        BbMatchHandlerContext context = BbMatchHandlerContext.getLocalContext();

        BbOrder4MatchBo order = context.allOpenOrders.get(orderId);
        if (null == order) {
            if (this.getCurrentMsgOffset() > context.getSentMqOffset()) {
                bbMatchedTaskService.addMatchedOrderCancelTask(context, this.getCurrentMsgOffset(), accountId, orderId, null);
            }
        } else {
            PriorityQueue<BbOrder4MatchBo> queue = context.getQueue(order.getBidFlag());
            queue.remove(order);
            context.allOpenOrders.remove(orderId);
            if (this.getCurrentMsgOffset() > context.getSentMqOffset()) {
                bbMatchedTaskService.addMatchedOrderCancelTask(context, this.getCurrentMsgOffset(), accountId, orderId, order.getNumber().subtract(order.getFilledNumber()));
            }
        }
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}