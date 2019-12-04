/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.bo.PcOrderSnapshotBo;
import com.hp.sh.expv3.match.component.notify.PcOrderMqNotify;
import com.hp.sh.expv3.match.config.setting.PcmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.constant.PcmatchConst;
import com.hp.sh.expv3.match.match.core.match.task.def.PcMatchTaskService;
import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.match.thread.impl.PcmatchOrderRmqConsumerThread;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import com.hp.sh.expv3.match.util.RedisKeyUtil;
import com.hp.sh.expv3.match.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.PriorityQueue;

@Service
@Scope("prototype")
public class PcOrderInitTask extends PcOrderBaseTask implements ApplicationContextAware {

    final Logger logger = LoggerFactory.getLogger(getClass());
    private IThreadWorker matchedThreadWorker;

    @Autowired
    private PcMatchTaskService pcMatchTaskService;

    public IThreadWorker getMatchedThreadWorker() {
        return matchedThreadWorker;
    }

    public void setMatchedThreadWorker(IThreadWorker matchedThreadWorker) {
        this.matchedThreadWorker = matchedThreadWorker;
    }

    private List<PcOrder4MatchBo> orders;

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

    @Override
    public void run() {
        Thread.currentThread().setName("PcMatWok-" + getAssetSymbol());
        PcMatchHandlerContext context = PcMatchHandlerContext.getLocalContext();
        /**
         * 作为下游的队列
         */
        context.setAssetSymbol(this.getAssetSymbol());
        context.setAsset(this.getAsset());
        context.setSymbol(this.getSymbol());
        context.matchedThreadWorker = this.matchedThreadWorker;

        if (orders == null) {
            loadData(context);
        }

        // booked add to queue
        // 属于平衡状态，理论上 所有booked状态的订单，应该都是创建时间早的，应该排是前面的

//        if (null != orders) {
//            for (PcOrderBo order : orders) {
//                if (pcOrderService.isPcOrderOk(order)) {
//                    if (PcOrderStatusEnum.CANCELED.test(order.getStatus())
//                            || PcOrderStatusEnum.FILLED.test(order.getStatus())
//                            || PcOrderStatusEnum.FAILED.test(order.getStatus())
//                            || PcOrderStatusEnum.EXPIRED.test(order.getStatus())
//                            ) {
//                        continue;
//                    } else {
//                        PcOrderStatusEnum displayStatus = PcOrderStatusEnum.getDisplayStatus(order.getStatus());
//                        if (displayStatus.equals(PcOrderStatusEnum.PENDING_CANCEL)
//                                || displayStatus.equals(PcOrderStatusEnum.PENDING_NEW)
//                                ) {
//                            logger.error("asset:{},symbol:{},accountId:{},orderId:{},pending status...", order.getAsset(), order.getSymbol(), order.getAccountId(), order.getId());
//                            throw new BaseBizRuntimeException(CommonCode.IMPOSSIBLE_LOGIC_EXCEPTION);
//                        }
//                    }
//                    PcOrder4MatchBo pcOrder4MatchBo = PcOrder4MatchBoUtil.buildOrder(order);
//                    PriorityQueue<PcOrder4MatchBo> queue = context.getQueue(pcOrder4MatchBo);
//                    queue.offer(pcOrder4MatchBo);
//                    context.allOpenOrders.put(pcOrder4MatchBo.getId(), pcOrder4MatchBo);
//                } else {
//                    logger.warn("{} {} {} {} order not ok,please fix", order.getAccountId(), order.getId(), order.getAsset(), order.getSymbol());
//                }
//            }
//        }

        IThreadWorker worker = threadManagerMatchImpl.getWorker(this.getAssetSymbol());

        PriorityQueue<PcOrder4MatchBo> bidOrderQueue = context.getQueue(CommonConst.YES);
        PriorityQueue<PcOrder4MatchBo> askOrderQueue = context.getQueue(CommonConst.NO);

        PcOrder4MatchBo bidOrder = bidOrderQueue.peek();
        PcOrder4MatchBo askOrder = askOrderQueue.peek();

        if (null == bidOrder || null == askOrder) {
            // 买卖深度有一方为空
        } else {
            if (bidOrder.getPrice().compareTo(askOrder.getPrice()) >= 0) {
                BigDecimal bidMaxPrice = bidOrder.getPrice();
                BigDecimal askMinPrice = askOrder.getPrice();

                if (bidMaxPrice.compareTo(askMinPrice) >= 0) {
                    logger.error("asset:{},symbol:{},accountId:{},orderId:{},bid order price >= top ask order.", bidOrder.getAsset(), bidOrder.getSymbol(), bidOrder.getAccountId(), bidOrder.getOrderId());
                    logger.error("asset:{},symbol:{},accountId:{},orderId:{},ask order price <= top bid order.", askOrder.getAsset(), askOrder.getSymbol(), askOrder.getAccountId(), askOrder.getOrderId());
                    throw new RuntimeException();
                }
            }
        }

        PcmatchOrderRmqConsumerThread orderConsumer = this.applicationContext.getBean(PcmatchOrderRmqConsumerThread.class);
        orderConsumer.setAsset(this.getAsset());
        orderConsumer.setSymbol(this.getSymbol());
        orderConsumer.setAssetSymbol(this.getAssetSymbol());
        orderConsumer.setInitOffset(this.getCurrentMsgOffset());
        orderConsumer.setName("PcMatchConsumer_" + getAsset() + "__" + this.getSymbol());
        orderConsumer.start();
        setSentMqOffset(context);

        pcOrderMqNotify.sendMatchStart(this.getAsset(), this.getSymbol());

    }

    private void setSentMqOffset(PcMatchHandlerContext context) {
        String sentMqOffsetRedisKey = RedisKeyUtil.buildOrderSentMqOffsetRedisKeyPattern(pcmatchPcmatchRedisKeySetting.getPcOrderSentMqOffsetRedisKeyPattern(), this.getAsset(), this.getSymbol());
        if (pcRedisUtil.exists(sentMqOffsetRedisKey)) {
            String s = pcRedisUtil.get(sentMqOffsetRedisKey);
            long sentOffset = Long.valueOf(s);
            context.setSentMqOffset(sentOffset);
        } else {
            context.setSentMqOffset(-1L);
        }
    }

    @Autowired
    private PcmatchRedisKeySetting pcmatchPcmatchRedisKeySetting;
    @Autowired
    private PcOrderMqNotify pcOrderMqNotify;

    @Autowired
    @Qualifier(PcmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil pcRedisUtil;

    void loadData(PcMatchHandlerContext context) {

        String snapshotRedisKey = RedisKeyUtil.buildPcOrderSnapshotRedisKey(pcmatchPcmatchRedisKeySetting.getPcOrderSnapshotRedisKeyPattern(), this.getAsset(), this.getSymbol());

        boolean exists = pcRedisUtil.exists(snapshotRedisKey);
        if (exists) {
            String s = pcRedisUtil.get(snapshotRedisKey);
            PcOrderSnapshotBo snapshot = JSON.parseObject(s, PcOrderSnapshotBo.class);
            context.setLastPrice(snapshot.getLastPrice());
            this.setCurrentMsgOffset(snapshot.getRmqNextOffset());

            List<PcOrder4MatchBo> limitAskOrders = snapshot.getLimitAskOrders();
            List<PcOrder4MatchBo> limitBidOrders = snapshot.getLimitBidOrders();

            if (null != limitAskOrders) {
                for (PcOrder4MatchBo askOrder : limitAskOrders) {
                    if (context.allOpenOrders.containsKey(askOrder.getOrderId())) {
                        logger.error("order in snapshot duplicated,asset:{},symbol:{},accountId:{},orderId:{}.", this.getAsset(), this.getSymbol(), askOrder.getAccountId(), askOrder.getOrderId());
                    } else {
                        context.limitAskQueue.add(askOrder);
                        context.allOpenOrders.put(askOrder.getOrderId(), askOrder);
                    }
                }
            }

            if (null != limitBidOrders) {
                for (PcOrder4MatchBo bidOrder : limitBidOrders) {
                    if (context.allOpenOrders.containsKey(bidOrder.getOrderId())) {
                        logger.error("order in snapshot duplicated,asset:{},symbol:{},accountId:{},orderId:{}.", this.getAsset(), this.getSymbol(), bidOrder.getAccountId(), bidOrder.getOrderId());
                    } else {
                        context.limitBidQueue.add(bidOrder);
                        context.allOpenOrders.put(bidOrder.getOrderId(), bidOrder);
                    }
                }
            }
        } else {
            context.setLastPrice(null);
            this.setCurrentMsgOffset(0L);
        }

        /**
         * 撮合使用批量化处理，book和trade 都是批量处理，统一发送
         */

        /**
         * 定时 将快照写入缓存或文件中
         *
         * OrderSnapshotCreate:{MqOffset:123,List<order>:[{},{}]}
         *
         * MqOffset
         * List<Order>
         * exMsgId，发送成功后回调方法中判断是否需要快照
         * lastSnapshotCompleted = false ，在回调时将快照保存，并且重置为true，以便下次快照任务生成
         *
         */

        /**
         * 收到创建快照消息后
         * 先确认lastSnapshotCompleted=true，否则什么也不做
         * lastSnapshotCompleted=true情况下，记录当前RMQ的MqOffset，并将List<Order> 深复制一份到快照中，
         * 此OrderSnapshotCreate消息对应的操作就是将数据持久化，完成之后再发一条 OrderSnapshotComplete 类型的消息（包含OrderMsgOffset记录，用于核对数据），再发到队列中
         * 当matcher收到 此OrderSnapshotComplete 后，对比MqOffset 无误后，将lastSnapshotCompleted = true，等待下次 OrderSnapshotCreate 消息
         *
         */
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}