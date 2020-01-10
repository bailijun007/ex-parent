/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.component.id.def.IdService;
import com.hp.sh.expv3.match.enums.IdTypeEnum;
import com.hp.sh.expv3.match.enums.PcOrderTypeEnum;
import com.hp.sh.expv3.match.match.core.match.handler.PcLimitOrderHandler;
import com.hp.sh.expv3.match.match.core.match.handler.PcMarketOrderHandler;
import com.hp.sh.expv3.match.match.core.match.handler.PcOrderHandler;
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

@Service
@Scope("prototype")
public class PcOrderNewTask extends PcOrderBaseTask implements ApplicationContextAware {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    private PcLimitOrderHandler pcLimitOrderHandler;
    @Autowired
    private PcMarketOrderHandler pcMarketOrderHandler;
    @Autowired
    private PcMatchedTaskService pcMatchedTaskService;

    private PcOrder4MatchBo order;

    public PcOrder4MatchBo getOrder() {
        return order;
    }

    public void setOrder(PcOrder4MatchBo order) {
        this.order = order;
    }

    @Autowired
    @Qualifier("threadManagerPcMatchImpl")
    private IThreadManager threadManagerMatchImpl;

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
        PcMatchHandlerContext context = PcMatchHandlerContext.getLocalContext();

        context.getMatchResult().setMatchTxId(idService.getId(IdTypeEnum.MATCH));

        while (true) {
            PcOrderHandler handler = null;
            if (PcOrderTypeEnum.LIMIT.getCode() == order.getOrderType()) {
                handler = pcLimitOrderHandler;
            } else if (PcOrderTypeEnum.MARKET.getCode() == order.getOrderType()) {
                handler = pcMarketOrderHandler;
            } else {
                throw new RuntimeException();
            }
            if (null != handler) {
                handler.process(context, order);
            }
            break;
        }
        if (this.getCurrentMsgOffset() > context.getSentMqOffset()) {
            pcMatchedTaskService.addMatchedOrderMatchedTask(context, this.getCurrentMsgOffset(), this.getCurrentMsgId(), order);
        }
        context.clear();
    }

}