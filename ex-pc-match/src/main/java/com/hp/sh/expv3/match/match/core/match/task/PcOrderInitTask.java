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
import com.hp.sh.expv3.match.util.DecimalUtil;
import com.hp.sh.expv3.match.util.PcOrder4MatchBoUtil;
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
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

        context.setAssetSymbol(this.getAssetSymbol());
        context.setAsset(this.getAsset());
        context.setSymbol(this.getSymbol());
        /**
         * 作为下游的队列
         */
        context.matchedThreadWorker = this.matchedThreadWorker;

        long sentMqOffset = getSentMqOffset();

        if (orders == null) {
            loadData(context, sentMqOffset);
        }

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
                    logger.error("bid asset:{},symbol:{},accountId:{},orderId:{},bid order price >= top ask order.{}", bidOrder.getAsset(), bidOrder.getSymbol(), bidOrder.getAccountId(), bidOrder.getOrderId(), DecimalUtil.toTrimLiteral(bidOrder.getPrice()));
                    logger.error("ask asset:{},symbol:{},accountId:{},orderId:{},ask order price <= top bid order.{}", askOrder.getAsset(), askOrder.getSymbol(), askOrder.getAccountId(), askOrder.getOrderId(), DecimalUtil.toTrimLiteral(askOrder.getPrice()));
                    throw new RuntimeException();
                }
            }
        }

        // 先发消息，所以一定会有topic，那么在topic消失的时候，一定是rebase的
        pcOrderMqNotify.sendMatchStart(this.getAsset(), this.getSymbol());

        PcmatchOrderRmqConsumerThread orderConsumer = this.applicationContext.getBean(PcmatchOrderRmqConsumerThread.class);
        orderConsumer.setAsset(this.getAsset());
        orderConsumer.setSymbol(this.getSymbol());
        orderConsumer.setAssetSymbol(this.getAssetSymbol());
        orderConsumer.setLastSentOffset(this.getCurrentMsgOffset());
        orderConsumer.setLastSentMsgId(this.getCurrentMsgId());
        orderConsumer.setName("PcMatchConsumer_" + getAsset() + "__" + this.getSymbol());
        orderConsumer.start();
        setSentMqOffset(context, sentMqOffset);

        logger.info("{} {} match start.", this.getAsset(), this.getSymbol());
    }

    private void setSentMqOffset(PcMatchHandlerContext context, long matchedMqOffset) {
        context.setSentMqOffset(matchedMqOffset);
    }

    private long getSentMqOffset() {
        String sentMqOffsetRedisKey = RedisKeyUtil.buildOrderSentMqOffsetRedisKeyPattern(pcmatchRedisKeySetting.getPcOrderSentMqOffsetRedisKeyPattern(), this.getAsset(), this.getSymbol());
        long matchedMqOffset = -1L;
        if (pcRedisUtil.exists(sentMqOffsetRedisKey)) {
            String s = pcRedisUtil.get(sentMqOffsetRedisKey);
            matchedMqOffset = Long.parseLong(s);
        }
        return matchedMqOffset;
    }

    @Autowired
    private PcmatchRedisKeySetting pcmatchRedisKeySetting;
    @Autowired
    private PcOrderMqNotify pcOrderMqNotify;

    @Autowired
    @Qualifier(PcmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil pcRedisUtil;

    void loadData(PcMatchHandlerContext context, long sentMqOffset) {

        String snapshotRedisKey = RedisKeyUtil.buildPcOrderSnapshotRedisKey(pcmatchRedisKeySetting.getPcOrderSnapshotRedisKeyPattern(), this.getAsset(), this.getSymbol());

        boolean exists = pcRedisUtil.exists(snapshotRedisKey);
        if (exists) {

            Set<String> snapshotOffsetList = pcRedisUtil.hkeys(snapshotRedisKey);

            TreeSet<Long> snapshotOffset = snapshotOffsetList.stream().map(Long::valueOf).collect(Collectors.toCollection(TreeSet::new));

            Long startSnapshotOffset = snapshotOffset.floor(sentMqOffset);

            if (null == startSnapshotOffset) {
                context.setLastPrice(null);
                this.setCurrentMsgOffset(0L);
                return;
            }

            String s = pcRedisUtil.hget(snapshotRedisKey, startSnapshotOffset + "");
            PcOrderSnapshotBo snapshot = JSON.parseObject(s, PcOrderSnapshotBo.class);
            context.setLastPrice(snapshot.getLastPrice());
            this.setCurrentMsgOffset(snapshot.getRmqCurrentOffset());

            List<PcOrder4MatchBo> limitAskOrders = snapshot.getLimitAskOrders();
            List<PcOrder4MatchBo> limitBidOrders = snapshot.getLimitBidOrders();

            if (null != limitAskOrders) {
                limitAskOrders.forEach(askOrder -> {
                    if (context.allOpenOrders.containsKey(askOrder.getOrderId())) {
                        logger.error("order in snapshot duplicated,asset:{},symbol:{},accountId:{},orderId:{}.", this.getAsset(), this.getSymbol(), askOrder.getAccountId(), askOrder.getOrderId());
                    } else {
                        PcOrder4MatchBoUtil.extendSetter(askOrder);
                        context.limitAskQueue.add(askOrder);
                        context.allOpenOrders.put(askOrder.getOrderId(), askOrder);
                    }
                });

            }

            if (null != limitBidOrders) {
                limitBidOrders.forEach(bidOrder -> {
                            if (context.allOpenOrders.containsKey(bidOrder.getOrderId())) {
                                logger.error("order in snapshot duplicated,asset:{},symbol:{},accountId:{},orderId:{}.", this.getAsset(), this.getSymbol(), bidOrder.getAccountId(), bidOrder.getOrderId());
                            } else {
                                PcOrder4MatchBoUtil.extendSetter(bidOrder);
                                context.limitBidQueue.add(bidOrder);
                                context.allOpenOrders.put(bidOrder.getOrderId(), bidOrder);
                            }
                        }
                );
            }

            /**
             * 删除大于此offset的快照信息
             */
            Set<String> ignoreSnapshotOffset = snapshotOffset.stream().filter(offset -> offset > startSnapshotOffset).map(String::valueOf).collect(Collectors.toSet());
            if (ignoreSnapshotOffset.size() > 0) {
                pcRedisUtil.hdel(snapshotRedisKey, ignoreSnapshotOffset);
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