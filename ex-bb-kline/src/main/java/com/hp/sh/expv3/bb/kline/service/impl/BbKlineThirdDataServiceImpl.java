package com.hp.sh.expv3.bb.kline.service.impl;

import com.hp.sh.expv3.bb.kline.constant.BbKLineKey;
import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.service.BbKlineThirdDataService;
import com.hp.sh.expv3.bb.kline.util.BbKlineRedisKeyUtil;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/11
 */
@Service
public class BbKlineThirdDataServiceImpl implements BbKlineThirdDataService {

    @Value("${bb.kline.bbGroupIds}")
    private Set<Integer> supportBbGroupIds;

    @Value("${bb.kline.triggerBatchSize}")
    private Integer triggerBatchSize;

    @Value("${bb.kline.supportFrequenceString}")
    private String supportFrequenceString;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Autowired
    @Qualifier("bbKlineOngoingRedisUtil")
    private RedisUtil bbKlineOngoingRedisUtil;

    @Value("${kline.bb.thirdDataUpdateEventPattern}")
    private String thirdDataUpdateEventPattern;
    @Value("${kline.bb.thirdDataPattern}")
    private String thirdDataPattern;
    @Value("${bb.kline}")
    private String bbKlinePattern;
    @Value("${bb.kline.updateEventPattern}")
    private String updateEventPattern;

    @Value("${bb.kline.thirdUpdate.enable}")
    private int thirdUpdateEnable;
    @Value("${bb.kline.thirdBatchSize}")
    private int thirdBatchSize;


    @Override
    public void updateKlineByThirdData() {

        List<BBSymbol> bbSymbols = listSymbol();
        List<BBSymbol> targetBbSymbols = filterBbSymbols(bbSymbols);

        for (BBSymbol bbSymbol : targetBbSymbols) {

            final String asset = bbSymbol.getAsset();
            final String symbol = bbSymbol.getSymbol();
            int freq = 1;
            String thirdDataUpdateEventKey = buildThirdDataUpdateEventKey(asset, symbol, freq);
            Long[] minAndMaxMs = listThirdUpdateEvent(thirdDataUpdateEventKey, thirdBatchSize);
            List<BBKLine> klines = listBbKline(asset, symbol, minAndMaxMs[0], minAndMaxMs[1], freq);
            coverData(asset, symbol, klines, freq);
            notifyKlineUpdate(asset, symbol, klines, freq);

        }


    }


    private void notifyKlineUpdate(String asset, String symbol, Integer targetFreq, Long startMinute) {
        //向集合中插入元素，并设置分数
        String key = BbKlineRedisKeyUtil.buildKlineUpdateEventRedisKey(updateEventPattern, asset, symbol, targetFreq);
        bbKlineOngoingRedisUtil.zadd(key, new HashMap<String, Double>() {{
                    put(BbKlineRedisKeyUtil.buildUpdateRedisMember(asset, symbol, targetFreq, startMinute), Long.valueOf(startMinute).doubleValue());
                }}
        );
    }


    /**
     * 覆盖
     *
     * @param asset
     * @param symbol
     * @param klines
     * @param freq
     */
    private void coverData(String asset, String symbol, List<BBKLine> klines, int freq) {
    }

    /**
     * 批量更新
     *
     * @param asset
     * @param symbol
     * @param klines
     * @param freq
     */
    private void notifyKlineUpdate(String asset, String symbol, List<BBKLine> klines, int freq) {
    }

    private List<BBKLine> listBbKline(String asset, String symbol, Long minAndMaxM, Long minAndMax, int freq) {
        String thirdDataKey = buildThirdDataKey(asset, symbol, freq);
        return null;
    }

    private String buildThirdDataKey(String asset, String symbol, int freq) {
        return "";
    }

    private Long[] listThirdUpdateEvent(String thirdDataUpdateEventKey, int thirdBatchSize) {
        return null;
    }

    private String buildThirdDataUpdateEventKey(String asset, String symbol, int freq) {
        // TODO xb
        return "";
    }

    private List<BBSymbol> listSymbol() {
        final Map<String, BBSymbol> key2Value = metadataRedisUtil.hgetAll(BbKLineKey.BB_SYMBOL, BBSymbol.class);
        List<BBSymbol> list = key2Value.values().stream().collect(Collectors.toList());
        return list;
    }

    private List<BBSymbol> filterBbSymbols(List<BBSymbol> bbSymbols) {
        return bbSymbols.stream()
                .filter(symbol -> supportBbGroupIds.contains(symbol.getBbGroupId()))
                .collect(Collectors.toList());
    }
}
