package com.hp.sh.expv3.bb.kline.service.impl;

import com.hp.sh.expv3.bb.kline.dao.BbRepairTradeMapper;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.service.BbRepairTradeService;
import com.hp.sh.expv3.bb.kline.util.BBKlineUtil;
import com.hp.sh.expv3.bb.kline.vo.BbRepairTradeVo;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author BaiLiJun  on 2020/3/13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbRepairTradeImpl implements BbRepairTradeService {
    @Autowired
    private BbRepairTradeMapper bbMergeTradeMapper;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Value("${bb.kline.bbGroupIds}")
    private Set<Integer> supportBbGroupIds;


//    @Scheduled(cron = "*/1 * * * * *")
    public void  execute(){
        List<BBSymbol> bbSymbols = BBKlineUtil.listSymbol(metadataRedisUtil);
        List<BBSymbol> targetBbSymbols = BBKlineUtil.filterBbSymbols(bbSymbols,supportBbGroupIds);


    }

}
