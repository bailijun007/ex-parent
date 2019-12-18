package com.hp.sh.expv3.pc.component.impl;

import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FeeRatioServiceImpl2 implements FeeRatioService {
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
        HashOperations hashOperations = stringRedisTemplate.opsForHash();
        Object pcFee = hashOperations.get("pc_fee", "t_" + userId);
        if (pcFee==null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(pcFee + "");
    }

    @Override
    public BigDecimal getCloseFeeRatio(long userId, String asset, String symbol) {

        return null;
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
    public BigDecimal getCloseFeeRatio(long userId) {
        return null;
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
        return null;
    }

    @Override
    public BigDecimal getMakerOpenFeeRatio(long userId, String asset, String symbol) {
        return null;
    }

    @Override
    public BigDecimal getMakerCloseFeeRatio(long userId, String asset, String symbol) {
        return null;
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
    public BigDecimal getMakerOpenFeeRatio(long userId) {
        return null;
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
    public BigDecimal getMakerCloseFeeRatio(long userId) {
        return null;
    }
}
