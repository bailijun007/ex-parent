package com.hp.sh.expv3.bb.kline.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hp.sh.expv3.bb.kline.dao.BbRepairTradeMapper;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.service.BbKlineRepairDataFromBbRepairTradeService;
import com.hp.sh.expv3.bb.kline.util.BBKlineUtil;
import com.hp.sh.expv3.bb.kline.util.BbKlineRedisKeyUtil;
import com.hp.sh.expv3.bb.kline.vo.BbRepairTradeVo;
import com.hp.sh.expv3.config.redis.RedisUtil;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.DecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Tuple;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author BaiLiJun  on 2020/3/13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbKlineRepairDataFromBbRepairTradeServiceImpl implements BbKlineRepairDataFromBbRepairTradeService {
    private static final Logger logger = LoggerFactory.getLogger(BbKlineRepairDataFromBbRepairTradeServiceImpl.class);

    @Autowired
    private BbRepairTradeMapper bbRepairTradeMapper;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Autowired
    @Qualifier("bbKlineOngoingRedisUtil")
    private RedisUtil bbRepairTradeUtil;

    @Value("${bb.kline.bbGroupIds}")
    private Set<Integer> supportBbGroupIds;

    @Value("${bb.kline2Trade.taskEventPattern}")
    private String kline2TradeTaskEventPattern;


    @Value("${from_exp.bbKlineTaskPattern}")
    private String fromExpBbKlineTaskPattern;

    @Value("${bb.kline.kline2TradeBatchSize}")
    private Integer kline2TradeBatchSize;

    @Value("${bb.kline.bbRepairTrade.enable}")
    private Integer bbRepairTradeEnable;


    @Scheduled(cron = "*/1 * * * * *")
    public void execute() {
        // int bbRepairTradeEnable=1;
        if (1 != bbRepairTradeEnable) {
            return;
        }

        List<BBSymbol> bbSymbols = BBKlineUtil.listSymbol(metadataRedisUtil);
        List<BBSymbol> targetBbSymbols = BBKlineUtil.filterBbSymbols(bbSymbols, supportBbGroupIds);

        for (BBSymbol bbSymbol : targetBbSymbols) {
            final String asset = bbSymbol.getAsset();
            final String symbol = bbSymbol.getSymbol();
            final int freq = 1;

            String taskKey = BbKlineRedisKeyUtil.buildKline2TradeTaskEventKey(kline2TradeTaskEventPattern, asset, symbol, freq);
            //监听通知消息
            final Set<Tuple> task = bbRepairTradeUtil.zpopmin(taskKey, kline2TradeBatchSize);
            if (CollectionUtils.isEmpty(task)) {
                continue;
            }

            String fromExpBbKlineTaskRedisKey = BbKlineRedisKeyUtil.buildFromExpBbKlineTaskRedisKey(fromExpBbKlineTaskPattern, asset, symbol, freq);
            for (Tuple tuple : task) {
                //[1483200240000,956.54,956.54,956.54,956.54,0.48375944]
                String element = tuple.getElement();
                JSONArray ja = JSON.parseArray(element);
                Long ms = ja.getLong(0);

                List<BbRepairTradeVo> trades = buildTradeList(ja, asset, symbol, ms);

                long endMs = TimeUnit.MINUTES.toMillis(TimeUnit.MILLISECONDS.toMinutes(ms) + 1) - 1;

                // 批量更新修正的交易记录表
              bbRepairTradeMapper.batchUpdate(trades, ms, endMs);

              // 批量保存
                bbRepairTradeMapper.batchSave(trades);

                //updateNotify
                HashMap<String, Double> scoreMembers = new HashMap<>();
                scoreMembers.put(ms + "", Long.valueOf(ms).doubleValue());
                bbRepairTradeUtil.zadd(fromExpBbKlineTaskRedisKey, scoreMembers);

            }
        }
    }


    private List<BbRepairTradeVo> buildTradeList(JSONArray ja, String asset, String symbol, Long ms) {
        List<BbRepairTradeVo> trades = new ArrayList<>();

        if (trades.stream().anyMatch(Objects::isNull)) {
            logger.warn("received kline2trade msg ,member has null.{}", ja.toJSONString());
            return trades;
        }

//[1577774220000,null,null,null,null,0] time,open,high,low,close,volume
        BigDecimal open = ja.getBigDecimal(1);
        BigDecimal high = ja.getBigDecimal(2);
        BigDecimal low = ja.getBigDecimal(3);
        BigDecimal close = ja.getBigDecimal(4);
        BigDecimal volume = ja.getBigDecimal(5);

        Set<String> priceCount = new HashSet<String>() {
            {
                add(DecimalUtil.toTrimLiteral(open));
                add(DecimalUtil.toTrimLiteral(low));
                add(DecimalUtil.toTrimLiteral(close));
                add(DecimalUtil.toTrimLiteral(high));
            }
        };
        BigDecimal v = volume.divide(BigDecimal.valueOf(priceCount.size()), 20, RoundingMode.HALF_UP).stripTrailingZeros();

        trades.add(this.buildRepairTrade(asset, symbol, v, open, ms));
        trades.add(this.buildRepairTrade(asset, symbol, v, high, ms + 15000));
        trades.add(this.buildRepairTrade(asset, symbol, v, low, ms + 30000));
        trades.add(this.buildRepairTrade(asset, symbol, v, close, ms + 59999));

        return trades;
    }

    private BbRepairTradeVo buildRepairTrade(String asset, String symbol, BigDecimal v, BigDecimal price, Long tradeInMs) {
        BbRepairTradeVo bbRepairTradeVo = new BbRepairTradeVo();
        bbRepairTradeVo.setMatchTxId(0L);
        bbRepairTradeVo.setTkBidFlag(0);
        bbRepairTradeVo.setTkAccountId(0L);
        bbRepairTradeVo.setTkOrderId(0L);
        bbRepairTradeVo.setMkAccountId(0L);
        bbRepairTradeVo.setMkOrderId(0L);
        bbRepairTradeVo.setPrice(price);
        bbRepairTradeVo.setNumber(v);
        bbRepairTradeVo.setTradeTime(tradeInMs);
        bbRepairTradeVo.setMakerHandleStatus(0);
        bbRepairTradeVo.setTakerHandleStatus(0);
        bbRepairTradeVo.setEnableFlag(IntBool.YES);
        long now = Instant.now().toEpochMilli();
        bbRepairTradeVo.setModified(now);
        bbRepairTradeVo.setCreated(now);
        bbRepairTradeVo.setAsset(asset);
        bbRepairTradeVo.setSymbol(symbol);
        return bbRepairTradeVo;
    }
}
