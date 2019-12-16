/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.matched.task.def.PcMatchedTaskService;
import com.hp.sh.expv3.match.mqmsg.PcPosLockedMqMsgDto;
import com.hp.sh.expv3.match.util.PcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope("prototype")
public class PcOrderCancel4LiqTask extends PcOrderBaseTask implements ApplicationContextAware {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private PcPosLockedMqMsgDto msg;

    public PcPosLockedMqMsgDto getMsg() {
        return msg;
    }

    public void setMsg(PcPosLockedMqMsgDto msg) {
        this.msg = msg;
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onSucess() {
    }

    @Override
    public boolean onError(Exception ex) {
        return false;
    }

    @Autowired
    private PcMatchedTaskService pcMatchedTaskService;

    @Override
    public void run() {

        PcMatchHandlerContext context = PcMatchHandlerContext.getLocalContext();

        int longFlag = msg.getLongFlag();

        Map<Long, PcOrder4MatchBo> id2OrderToCancel = new HashMap<>();

        if (null != context.limitAskQueue && (!context.limitAskQueue.isEmpty())) {
            for (PcOrder4MatchBo order : context.limitAskQueue) {
                if (msg.getAccountId() == order.getAccountId() && longFlag == PcUtil.getLongFlag(order.getCloseFlag(), order.getBidFlag()) && PcUtil.isClose(order.getCloseFlag())) {
                    id2OrderToCancel.put(order.getOrderId(), order);
                }
            }
        }
        if (null != context.limitBidQueue && (!context.limitBidQueue.isEmpty())) {
            for (PcOrder4MatchBo order : context.limitBidQueue) {
                if (msg.getAccountId() == order.getAccountId() && longFlag == PcUtil.getLongFlag(order.getCloseFlag(), order.getBidFlag()) && PcUtil.isClose(order.getCloseFlag())) {
                    id2OrderToCancel.put(order.getOrderId(), order);
                }
            }
        }

        for (PcOrder4MatchBo order : context.allOpenOrders.values()) {
            if (msg.getAccountId() == order.getAccountId() && longFlag == PcUtil.getLongFlag(order.getCloseFlag(), order.getBidFlag()) && PcUtil.isClose(order.getCloseFlag())) {
                if (!id2OrderToCancel.containsKey(order.getOrderId())) {
                    logger.warn("order {} in all order,but not in priority queue", order.getOrderId());
                    id2OrderToCancel.put(order.getOrderId(), order);
                }
            }
        }

        for (PcOrder4MatchBo order : id2OrderToCancel.values()) {
            if (msg.getAccountId() == order.getAccountId() && longFlag == PcUtil.getLongFlag(order.getCloseFlag(), order.getBidFlag()) && PcUtil.isClose(order.getCloseFlag())) {
                if (!context.allOpenOrders.containsKey(order.getOrderId())) {
                    logger.warn("order {} in priority queue but not in all order.", order.getOrderId());
                }
            }
        }

        // 从queue 中删除
        // context.allOpenOrders 中删除
        for (PcOrder4MatchBo order : id2OrderToCancel.values()) {
            context.allOpenOrders.remove(order.getOrderId());
            context.getQueue(order.getBidFlag()).remove(order);
        }

        // book 信息懒处理，交由 matched 线程处理
        if (this.getCurrentMsgOffset() > context.getSentMqOffset()) {
            // 加入任务中
            pcMatchedTaskService.addMatchedOrderCancelByLiqTask(context, this.getCurrentMsgOffset(), id2OrderToCancel.values(), msg);
        }
    }

}