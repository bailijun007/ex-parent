package com.hp.sh.expv3.pc.component.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.pc.strategy.vo.PosLevelVo;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FeeRatioServiceImpl2 implements FeeRatioService {
    private final String key ="pc_fee";

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
        List<Object> list = new ArrayList<>();
        list.add(asset + "__" + symbol);
        List<PosLevelVo> list1 = hashOperations.multiGet("pc_pos_level", list);
        if (CollectionUtils.isEmpty(list1)) {
            return BigDecimal.ZERO;
        }
        //   list1.stream().filter(vo -> vo.getMinAmt().compareTo(volume)<=0).filter(vo -> vo.getMaxAmt().compareTo(volume)>=0).map(PosLevelVo::getPosHoldMarginRatio).collect();
        for (PosLevelVo vo : list1) {
            if (vo.getMinAmt().compareTo(volume) <= 0 && vo.getMaxAmt().compareTo(volume) >= 0) {
                return vo.getPosHoldMarginRatio();
            }
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

