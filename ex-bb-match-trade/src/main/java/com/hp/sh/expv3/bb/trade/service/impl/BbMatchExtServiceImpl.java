package com.hp.sh.expv3.bb.trade.service.impl;

import com.hp.sh.expv3.bb.trade.dao.BbMatchExtMapper;
import com.hp.sh.expv3.bb.trade.pojo.BbMatchExtVo;
import com.hp.sh.expv3.bb.trade.service.BbMatchExtService;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author BaiLiJun  on 2020/3/31
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbMatchExtServiceImpl implements BbMatchExtService {

    @Autowired
    private BbMatchExtMapper bbMatchExtMapper;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;


    @Override
    public void batchSave(List<BbMatchExtVo> trades, String table) {
        if (CollectionUtils.isEmpty(trades)) {
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.now();
        String format = localDate.format(dtf);
        BbMatchExtVo bbMatchExtVo = trades.get(0);
        String asset = bbMatchExtVo.getAsset();
        String symbol = bbMatchExtVo.getSymbol();
        bbMatchExtMapper.batchSave(trades, table);
        int size = trades.size();
        String key = "bb:matchCount:" + asset + ":" + symbol + ":" + format;
        metadataRedisUtil.incrBy(key, size);
    }

}
