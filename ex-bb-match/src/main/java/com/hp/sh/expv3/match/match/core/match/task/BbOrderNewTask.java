/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.component.id.def.IdService;
import com.hp.sh.expv3.match.enums.BbOrderTypeEnum;
import com.hp.sh.expv3.match.enums.IdTypeEnum;
import com.hp.sh.expv3.match.match.core.match.handler.BbLimitOrderHandler;
import com.hp.sh.expv3.match.match.core.match.handler.BbMarketOrderHandler;
import com.hp.sh.expv3.match.match.core.match.handler.BbOrderHandler;
import com.hp.sh.expv3.match.match.core.match.thread.BbMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.matched.task.def.BbMatchedTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class BbOrderNewTask extends BbOrderBaseTask implements ApplicationContextAware {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    private BbLimitOrderHandler bbLimitOrderHandler;
    @Autowired
    private BbMarketOrderHandler bbMarketOrderHandler;
    @Autowired
    private BbMatchedTaskService bbMatchedTaskService;

    private BbOrder4MatchBo order;

    public BbOrder4MatchBo getOrder() {
        return order;
    }

    public void setOrder(BbOrder4MatchBo order) {
        this.order = order;
    }

    @Override
    public void onSucess() {
    }

    @Override
    public boolean onError(Exception ex) {
        return false;
    }

    @Autowired
    private IdService idService;

    @Override
    public void run() {

        long now = System.currentTimeMillis();
        BbMatchHandlerContext context = BbMatchHandlerContext.getLocalContext();

        context.getMatchResult().setMatchTxId(idService.getId(IdTypeEnum.MATCH));

        while (true) {
            BbOrderHandler handler = null;
            if (BbOrderTypeEnum.LIMIT.getCode() == order.getOrderType()) {
                handler = bbLimitOrderHandler;
            } else if (BbOrderTypeEnum.MARKET.getCode() == order.getOrderType()) {
                handler = bbMarketOrderHandler;
            } else {
                throw new RuntimeException();
            }
            if (null != handler) {
                handler.process(context, order);
            }
            break;
        }
        if (this.getCurrentMsgOffset() > context.getSentMqOffset()) {
            bbMatchedTaskService.addMatchedOrderMatchedTask(context, this.getCurrentMsgOffset(), this.getCurrentMsgId(), order);
        }
        context.clear();
    }

}