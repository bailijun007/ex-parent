package com.hp.sh.expv3.pc.trade.service.impl;

import com.hp.sh.expv3.config.redis.RedisUtil;
import com.hp.sh.expv3.pc.trade.service.PcMatchExtService;
import com.hp.sh.expv3.pc.trade.dao.PcMatchExtMapper;
import com.hp.sh.expv3.pc.trade.pojo.PcMatchExtVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcMatchExtServiceImpl implements PcMatchExtService {

    @Autowired
    private PcMatchExtMapper bbMatchExtMapper;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Override
    public void batchSave(List<PcMatchExtVo> trades, String table) {
        if (CollectionUtils.isEmpty(trades)) {
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.now();
        String format = localDate.format(dtf);
        PcMatchExtVo pcMatchExtVo = trades.get(0);
        bbMatchExtMapper.batchSave(trades, table);
        int size = trades.size();
        String key = "bb:matchCount:" + pcMatchExtVo.getAsset() + ":" + pcMatchExtVo.getSymbol() + ":" + format;
        metadataRedisUtil.incrBy(key, size);
    }

}
