package com.hp.sh.expv3.bb.kline.service.impl;

import com.hp.sh.expv3.bb.kline.constant.BbKLineKey;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
import com.hp.sh.expv3.bb.kline.pojo.PcSymbol;
import com.hp.sh.expv3.bb.kline.service.SupportBbGroupIdsJobService;
import com.hp.sh.expv3.config.redis.RedisUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/18
 */
@Service
public class SupportBbGroupIdsJobServiceImpl implements SupportBbGroupIdsJobService {
    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Value("${bb.kline.symbols}")
    private String symbols;

    @Override
    @Scheduled(cron = "*/1 * * * * *")
    public Map<Integer, List<BBSymbol>> listSymbols() {
        if (symbols.equals("bb_symbol")) {
            Map<String, BBSymbol> symbolMap = metadataRedisUtil.hgetAll(symbols, BBSymbol.class);
            if (!CollectionUtils.isEmpty(symbolMap)) {
                List<BBSymbol> list = symbolMap.values().stream().collect(Collectors.toList());
                Map<Integer, List<BBSymbol>> map = list.stream().collect(Collectors.groupingBy(bbSymbol -> bbSymbol.getBbGroupId()));
                return map;
            }
        } else if (symbols.equals("pc_contract")) {
            Map<String, PcSymbol> symbolMap = metadataRedisUtil.hgetAll(symbols, PcSymbol.class);
            if (!CollectionUtils.isEmpty(symbolMap)) {
                List<PcSymbol> pcSymbols = symbolMap.values().stream().collect(Collectors.toList());
                List<BBSymbol> bbSymbols = this.converPcSymbols(pcSymbols);
                Map<Integer, List<BBSymbol>> map = bbSymbols.stream().collect(Collectors.groupingBy(bbSymbol -> bbSymbol.getBbGroupId()));
                return map;
            }
        }
        return null;
    }

    private List<BBSymbol> converPcSymbols(List<PcSymbol> pcSymbols) {
        List<BBSymbol> list = new ArrayList<>();
        for (PcSymbol pcSymbol : pcSymbols) {
            BBSymbol bbSymbol = new BBSymbol();
            BeanUtils.copyProperties(pcSymbol, bbSymbol);
            bbSymbol.setBbGroupId(pcSymbol.getContractGroup());
            list.add(bbSymbol);
        }
        return list;
    }


}
