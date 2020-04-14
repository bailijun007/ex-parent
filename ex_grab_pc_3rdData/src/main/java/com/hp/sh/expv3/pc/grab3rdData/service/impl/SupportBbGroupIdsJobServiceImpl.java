package com.hp.sh.expv3.pc.grab3rdData.service.impl;

import com.hp.sh.expv3.config.redis.RedisUtil;
import com.hp.sh.expv3.pc.grab3rdData.pojo.BBSymbol;
import com.hp.sh.expv3.pc.grab3rdData.pojo.PcSymbol;
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

    @Value("${pc.symbols}")
    private String symbols;

    @Value("${pc.bbGroupIds}")
    private Integer bbGroupId;

  static    Map<Integer, List<PcSymbol>> map = new ConcurrentHashMap<>();

    @Override
    @Scheduled(cron = "*/1 * * * * *")
    public Map<Integer, List<PcSymbol>> listSymbols() {
        if (symbols.equals("pc_contract")) {
            Map<String, PcSymbol> symbolMap = metadataRedisUtil.hgetAll(symbols, PcSymbol.class);
            if (!CollectionUtils.isEmpty(symbolMap)) {
                List<PcSymbol> list = symbolMap.values().stream().collect(Collectors.toList());
                map = list.stream().collect(Collectors.groupingBy(pcSymbol->pcSymbol.getSymbolType()));
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
    public List<PcSymbol> getSymbols() {
        List<PcSymbol> list = new CopyOnWriteArrayList<>();
        if (CollectionUtils.isEmpty(map)){
            map = listSymbols();
        }
        if (!CollectionUtils.isEmpty(map)) {
            for (Integer integer : map.keySet()) {
//                if (bbGroupId.equals(integer)) {
                    List<PcSymbol> bbSymbols = map.get(integer);
                    for (PcSymbol bbSymbol : bbSymbols) {
                        list.add(bbSymbol);
                    }
//                }
            }
        }
        return list;
    }


}
