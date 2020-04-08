package com.hp.sh.expv3.bb.trade.service.impl;

import com.hp.sh.expv3.bb.trade.pojo.BBSymbol;
import com.hp.sh.expv3.bb.trade.pojo.PcSymbol;
import com.hp.sh.expv3.bb.trade.service.SupportBbGroupIdsJobService;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/18
 */
@Service
public class SupportBbGroupIdsJobServiceImpl implements SupportBbGroupIdsJobService {
    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Value("${bb.trade.symbols}")
    private String symbols;

    @Value("${bb.trade.bbGroupIds}")
    private Integer bbGroupId;

    Map<Integer, List<BBSymbol>> map = new ConcurrentHashMap<>();

    @Override
    @Scheduled(cron = "*/1 * * * * *")
    public Map<Integer, List<BBSymbol>> listSymbols() {
        if (symbols.equals("bb_symbol")) {
            Map<String, BBSymbol> symbolMap = metadataRedisUtil.hgetAll(symbols, BBSymbol.class);
            if (!CollectionUtils.isEmpty(symbolMap)) {
                List<BBSymbol> list = symbolMap.values().stream().collect(Collectors.toList());
                map = list.stream().collect(Collectors.groupingBy(bbSymbol -> bbSymbol.getBbGroupId()));
                return map;
            }
        } else if (symbols.equals("pc_contract")) {
            Map<String, PcSymbol> symbolMap = metadataRedisUtil.hgetAll(symbols, PcSymbol.class);
            if (!CollectionUtils.isEmpty(symbolMap)) {
                List<PcSymbol> pcSymbols = symbolMap.values().stream().collect(Collectors.toList());
                List<BBSymbol> bbSymbols = this.converPcSymbols(pcSymbols);
                map = bbSymbols.stream().collect(Collectors.groupingBy(bbSymbol -> bbSymbol.getBbGroupId()));
                return map;
            }
        }
        return null;
    }


    /**
     * @return USDT__ETC_USDT
     */
    @Override
    public List<BBSymbol> getSymbols() {
        List<BBSymbol> list = new CopyOnWriteArrayList<>();
        if (!CollectionUtils.isEmpty(map)) {
            for (Integer integer : map.keySet()) {
                if(bbGroupId.equals(integer)){
                    List<BBSymbol> bbSymbols = map.get(integer);
                    for (BBSymbol bbSymbol : bbSymbols) {
                        list.add(bbSymbol);
                    }
                }
            }
        }
        return list;
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
