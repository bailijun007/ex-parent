/**
 * @author 10086
 * @date 2019/10/24
 */
package com.hp.sh.expv3.match.match.core.match.thread.impl;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.component.notify.PcOrderMqNotify;
import com.hp.sh.expv3.match.config.setting.PcmatchRocketMqSetting;
import com.hp.sh.expv3.match.config.setting.RocketMqSetting;
import com.hp.sh.expv3.match.enums.RmqTagEnum;
import com.hp.sh.expv3.match.match.core.match.task.PcOrderBaseTask;
import com.hp.sh.expv3.match.match.core.match.task.def.PcMatchTaskService;
import com.hp.sh.expv3.match.mqmsg.PcOrderMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.PcPosLockedMqMsgDto;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import com.hp.sh.expv3.match.util.PcOrder4MatchBoUtil;
import com.hp.sh.expv3.match.util.PcRocketMqUtil;
import com.hp.sh.expv3.match.util.PcUtil;
import com.hp.sh.expv3.match.util.Tuple2;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Scope("prototype")
public class PcmatchOrderRmqConsumerThread extends Thread {

    final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RocketMqSetting rocketMqSetting;
    @Autowired
    private PcmatchRocketMqSetting pcmatchRocketMqSetting;
    @Autowired
    private PcMatchTaskService pcMatchTaskService;

    private long initOffset;
    private String asset;
    private String symbol;
    private String assetSymbol;

    public long getInitOffset() {
        return initOffset;
    }

    public void setInitOffset(long initOffset) {
        this.initOffset = initOffset;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
    }

    @Autowired
    @Qualifier("threadManagerPcMatchImpl")
    private IThreadManager threadManagerPcMatchImpl;

    @PostConstruct
    private void init() {
    }

    private AtomicBoolean isStart = new AtomicBoolean(false);

    @Override
    public void run() {
//
//        while (!isStart.get()) {
//        }
//        isStart.set(true);
        try {
            startListener();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        while (true) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
            }
        }

    }

    private void startListener() {

        String consumerGroup = PcRocketMqUtil.buildPcOrderConsumerGroupName(pcmatchRocketMqSetting.getPcOrderConsumerGroupName(), this.asset, this.symbol);
        String instanceName = PcRocketMqUtil.buildPcOrderConsumerInstanceName(pcmatchRocketMqSetting.getPcOrderConsumerInstanceName(), this.asset, this.symbol);
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer(consumerGroup);
        consumer.setNamesrvAddr(rocketMqSetting.getNameSrvAddr());
        consumer.setNamespace(rocketMqSetting.getNameSpace());

        consumer.setVipChannelEnabled(false);
        consumer.setInstanceName(instanceName);
        consumer.setConsumerPullTimeoutMillis(pcmatchRocketMqSetting.getMatchPullTimeoutInMs());

        try {
            consumer.start();
        } catch (MQClientException e) {
            logger.error(e.getErrorMessage(), e);
        }

        try {


            Map<String, String> topic2AssetSymbol = new HashMap<>();

            Map<String, Long> topic2Offset = new HashMap<>();
            Tuple2<String, String> assetSymbolTuple = PcUtil.splitAssetAndSymbol(assetSymbol);
            String topicName = PcRocketMqUtil.buildPcOrderTopicName(pcmatchRocketMqSetting.getPcOrderTopicNamePattern(), assetSymbolTuple.first, assetSymbolTuple.second);

            Set<MessageQueue> mqs = getMqs(consumer, topicName);
            topic2Offset.put(topicName, initOffset);
            topic2AssetSymbol.put(topicName, assetSymbol);

            while (true) {

                for (MessageQueue mq : mqs) {
//                	long offset = consumer.fetchConsumeOffset(mq, true);
//                	PullResultExt pullResult =(PullResultExt)consumer.pull(mq, null, getMessageQueueOffset(mq), 32);
                    //消息未到达默认是阻塞10秒，private long consumerPullTimeoutMillis = 1000 * 10;
//                    logger.info("{}:{}", mq.getTopic(), topic2Offset.get(mq.getTopic()));
                    try {
                        String assetSymbol = topic2AssetSymbol.get(topicName);
                        String asset = assetSymbolTuple.first;
                        String symbol = assetSymbolTuple.second;

                        PullResult pullResult = consumer.pull(mq,
                                String.join("||",
                                        RmqTagEnum.PC_BOOK_RESET.getConstant(),
                                        RmqTagEnum.PC_ORDER_PENDING_CANCEL.getConstant(),
                                        RmqTagEnum.PC_MATCH_ORDER_SNAPSHOT_CREATE.getConstant(),
                                        RmqTagEnum.PC_ORDER_PENDING_NEW.getConstant(),
                                        RmqTagEnum.PC_POS_LIQ_LOCKED.getConstant(),
                                        RmqTagEnum.PC_ORDER_REBASE.getConstant()
                                ),
                                topic2Offset.get(topicName),
                                1024);

                        List<MessageExt> msgFoundList = pullResult.getMsgFoundList();
                        if (null == msgFoundList || msgFoundList.isEmpty()) {
                            // 本批没有取到任何数据，期待下一次
                            topic2Offset.put(topicName, pullResult.getNextBeginOffset());
                            try {
                                Thread.sleep(10L);
                            } catch (InterruptedException e) {// catched ?
                                e.printStackTrace();
                            }
                        } else {

                            if (logger.isDebugEnabled()) {
                                logger.debug("{}:size:{} {}-》{},last {}", topicName, msgFoundList.size(), topic2Offset.get(topicName), pullResult.getNextBeginOffset(), msgFoundList.get(msgFoundList.size() - 1).getQueueOffset());
                            }

                            for (int i = 0; i < msgFoundList.size(); i++) {
                                MessageExt m = msgFoundList.get(i);
                                topic2Offset.put(topicName, m.getQueueOffset() + 1);
                                String body = new String(m.getBody());

                                IThreadWorker matchWorker = threadManagerPcMatchImpl.getWorker(assetSymbol);

                                PcOrderBaseTask task = null;
                                long queueOffset = m.getQueueOffset();
                                // 按出现频率多少，排 if 的顺序
                                if (RmqTagEnum.PC_ORDER_PENDING_NEW.getConstant().equals(m.getTags())) {
                                    PcOrderMqMsgDto dto = JSON.parseObject(body, PcOrderMqMsgDto.class);
                                    if (null == dto.getFilledNumber()) {
                                        dto.setFilledNumber(BigDecimal.ZERO);
                                    }
                                    PcOrder4MatchBo pcOrder4Match = PcOrder4MatchBoUtil.convert(dto);
                                    task = pcMatchTaskService.buildPcOrderNewTask(assetSymbol, asset, symbol, queueOffset, pcOrder4Match);
                                    matchWorker.addTask(task);
                                } else if (RmqTagEnum.PC_ORDER_PENDING_CANCEL.getConstant().equals(m.getTags())) {
                                    PcOrderMqMsgDto dto = JSON.parseObject(body, PcOrderMqMsgDto.class);
                                    task = pcMatchTaskService.buildPcOrderCancelTask(assetSymbol, asset, symbol, queueOffset, dto.getAccountId(), dto.getOrderId());
                                    task.setCurrentMsgOffset(m.getQueueOffset());
                                    matchWorker.addTask(task);
                                } else if (RmqTagEnum.PC_BOOK_RESET.getConstant().equals(m.getTags())) {
                                    task = pcMatchTaskService.buildPcOrderBookReset(assetSymbol, asset, symbol, queueOffset);
                                    matchWorker.addTask(task);
                                } else if (RmqTagEnum.PC_MATCH_ORDER_SNAPSHOT_CREATE.getConstant().equals(m.getTags())) {
                                    task = pcMatchTaskService.buildOrderSnapshotTask(assetSymbol, asset, symbol, queueOffset);
                                    matchWorker.addTask(task);
                                } else if (RmqTagEnum.PC_POS_LIQ_LOCKED.getConstant().equals(m.getTags())) {
                                    PcPosLockedMqMsgDto dto = JSON.parseObject(body, PcPosLockedMqMsgDto.class);
                                    task = pcMatchTaskService.buildPcOrderCancelByLiqTask(assetSymbol, asset, symbol, queueOffset, dto);
                                    matchWorker.addTask(task);
                                } else if (RmqTagEnum.PC_ORDER_REBASE.getConstant().equals(m.getTags())) {
                                    task = pcMatchTaskService.buildOrderRebaseTask(assetSymbol, asset, symbol, queueOffset);
                                    matchWorker.addTask(task);
                                } else {
                                    logger.error("get tags {} not define,go to exit -1", m.getTags());
                                    System.exit(-1);
                                }
                            }
                            try {
                                Thread.sleep(2L);
                            } catch (InterruptedException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    } catch (MQBrokerException e) {
                        if (e.getResponseCode() == ResponseCode.TOPIC_NOT_EXIST) {
                            logger.warn("{} deleted.rebase ", assetSymbolTuple);
                            pcOrderMqNotify.sendPcOrderRebase(asset, symbol);
                            mqs = getMqs(consumer, topicName);
                        } else {
                            logger.error(e.getErrorMessage());
                            Thread.sleep(1000L);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(this.getName() + " stop:" + e.getMessage(), e);
            isStart.set(false);
        }
        isStart.set(false);
    }

    @Autowired
    private PcOrderMqNotify pcOrderMqNotify;

    protected Set<MessageQueue> getMqs(DefaultMQPullConsumer consumer, String topicName) {

        Set<MessageQueue> mqs = new HashSet<>();
        while (true) {
            try {
                Set<MessageQueue> qs = consumer.fetchSubscribeMessageQueues(topicName);
                if (qs.size() > 1) {
                    logger.error("rmq topic " + topicName + " config more than 1 queue");
                    System.exit(-1);
                } else {
                    mqs.addAll(qs);
                    break;
                }
            } catch (MQClientException e) {
                if (e.getCause() instanceof MQClientException) {
                    MQClientException o = (MQClientException) e.getCause();
                    if (o.getResponseCode() == ResponseCode.TOPIC_NOT_EXIST) {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e1) {
                        }
                        continue;
                    }
                }
                logger.error(e.getErrorMessage(), e);
                System.exit(-1);
            }
        }
        return mqs;
    }

}