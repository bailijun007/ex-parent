package com.hp.sh.expv3.pc.grab3rdData.service.impl;

import com.hp.sh.expv3.config.redis.RedisUtil;
import com.hp.sh.expv3.pc.grab3rdData.pojo.BBSymbol;
import com.hp.sh.expv3.pc.grab3rdData.service.SupportBbGroupIdsJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
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

  static    Map<Integer, List<BBSymbol>> map = new ConcurrentHashMap<>();

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
        }
        return null;
    }


    /**
     * @return USDT__ETC_USDT
     */
    @Override
    @PostConstruct
    public List<BBSymbol> getSymbols() {
        List<BBSymbol> list = new CopyOnWriteArrayList<>();
        if (CollectionUtils.isEmpty(map)){
            map = listSymbols();
        }
        if (!CollectionUtils.isEmpty(map)) {
            for (Integer integer : map.keySet()) {
                if (bbGroupId.equals(integer)) {
                    List<BBSymbol> bbSymbols = map.get(integer);
                    for (BBSymbol bbSymbol : bbSymbols) {
                        list.add(bbSymbol);
                    }
                }
            }
        }
        return list;
    }

//    private List<BBSymbol> converPcSymbols(List<PcSymbol> pcSymbols) {
//        List<BBSymbol> list = new ArrayList<>();
//        for (PcSymbol pcSymbol : pcSymbols) {
//            BBSymbol bbSymbol = new BBSymbol();
//            BeanUtils.copyProperties(pcSymbol, bbSymbol);
//            bbSymbol.setBbGroupId(pcSymbol.getContractGroup());
//            list.add(bbSymbol);
//        }
//        return list;
//    }


}
