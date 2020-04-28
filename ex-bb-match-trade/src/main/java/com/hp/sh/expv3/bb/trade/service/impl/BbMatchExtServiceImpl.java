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




    @Override
    public int batchSave(List<BbMatchExtVo> trades, String table) {
        if (CollectionUtils.isEmpty(trades)) {
            return 0;
        }

        bbMatchExtMapper.batchSave(trades, table);
        return trades.size();
    }

}
