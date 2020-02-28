package com.hp.sh.expv3.bb.component.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.component.FeeRatioService;
import com.hp.sh.expv3.bb.constant.RedisKey;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Component
public class FeeRatioServiceImpl implements FeeRatioService {
	private static final Logger logger = LoggerFactory.getLogger(FeeRatioServiceImpl.class);

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Resource(name = "templateDB5")
    private StringRedisTemplate templateDB5;


    /**
     * redis :
     * db: 0
     * redis key:bb_fee
     * hash key: t__${userId}
     * 取 value
     *
     * @param userId
     * @return
     */
    @Override
    public BigDecimal getTakerFeeRatio(long userId, String asset, String symbol) {
        return findFeeRatio(userId, RedisKey.BB_FEE, RedisKey.KEY_PREFIX_TAKER,templateDB0);
    }

    /**
     * redis :
     * db: 0
     * redis key:bb_fee
     * hash key: m__${userId}
     * 取 value
     *
     * @param userId
     * @return
     */
    @Override
    public BigDecimal getMakerFeeRatio(long userId, String asset, String symbol) {
        return findFeeRatio(userId, RedisKey.BB_FEE, RedisKey.KEY_PREFIX_MAKER,templateDB0);
    }

	private BigDecimal findFeeRatio(long userId, String key, String prefix,StringRedisTemplate template) {
        HashOperations hashOperations = template.opsForHash();
        Object bbFee = hashOperations.get(key, prefix + userId);
        if (bbFee == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(bbFee + "");
    }

}

