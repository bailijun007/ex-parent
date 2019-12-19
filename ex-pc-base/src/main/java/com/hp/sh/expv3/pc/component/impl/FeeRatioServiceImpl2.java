package com.hp.sh.expv3.pc.component.impl;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.strategy.vo.PosLevelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FeeRatioServiceImpl2 implements FeeRatioService {
    private final String key = "pc_fee";

    @Autowired
    private PcPositionDAO pcPositionDAO;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public BigDecimal getInitedMarginRatio(BigDecimal leverage) {
        return BigDecimal.ONE.divide(leverage, Precision.PERCENT_PRECISION, Precision.LESS);
    }

    /**
     * redis :
     * db: 0
     * redis key:pc_fee
     * hash key: t__${userId}
     * 取 value
     *
     * @param userId
     * @return
     */
    @Override
    public BigDecimal getOpenFeeRatio(long userId, String asset, String symbol) {
        String prefix = "t_";
        return findFeeRatio(userId, key, prefix);
    }

    /**
     * redis :
     * db: 0
     * redis key:pc_fee
     * hash key: t__${userId}
     * 取 value
     *
     * @param userId
     * @return
     */
    @Override
    public BigDecimal getCloseFeeRatio(long userId, String asset, String symbol) {
        String prefix = "t_";
        return findFeeRatio(userId, key, prefix);
    }


    /**
     * redis :
     * db: 0
     * redis key:pc_pos_level
     * hash key: ${asset}__${symbol}
     * 取 数组中的 minAmt <= ${volume} <= maxAmt 那条记录的 minHoldMarginRatio的值返回
     *
     * @param userId
     * @param asset
     * @param symbol
     * @param volume
     * @return
     */
    @Override
    public BigDecimal getHoldRatio(Long userId, String asset, String symbol, BigDecimal volume) {
        HashOperations hashOperations = stringRedisTemplate.opsForHash();
        String hashKey = asset + "__" + symbol;
        Object s = hashOperations.get("pc_pos_level", hashKey);
        if (null != s) {
            List<PosLevelVo> voList = JSON.parseArray(s.toString(), PosLevelVo.class);
            List<BigDecimal> collect = voList.stream().filter(vo -> vo.getMinAmt().compareTo(volume) <= 0 && vo.getMaxAmt().compareTo(volume) >= 0)
                    .map(PosLevelVo::getMinHoldMarginRatio).collect(Collectors.toList());
            return collect.get(0);
        }

        return BigDecimal.ZERO;
    }

    /**
     * redis :
     * db: 0
     * redis key:pc_fee
     * hash key: m__${userId}
     * 取 value
     *
     * @param userId
     * @return
     */
    @Override
    public BigDecimal getMakerOpenFeeRatio(long userId, String asset, String symbol) {
        String prefix = "m_";
        return findFeeRatio(userId, key, prefix);
    }

    /**
     * redis :
     * db: 0
     * redis key:pc_fee
     * hash key: m__${userId}
     * 取 value
     *
     * @param userId
     * @return
     */
    @Override
    public BigDecimal getMakerCloseFeeRatio(long userId, String asset, String symbol) {
        String prefix = "m_";
        return findFeeRatio(userId, key, prefix);
    }


    private BigDecimal findFeeRatio(long userId, String key, String prefix) {
        HashOperations hashOperations = stringRedisTemplate.opsForHash();
        Object pcFee = hashOperations.get(key, prefix + userId);
        if (pcFee == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(pcFee + "");
    }

}

