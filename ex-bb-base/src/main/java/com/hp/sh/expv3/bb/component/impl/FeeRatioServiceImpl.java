package com.hp.sh.expv3.bb.component.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.component.FeeRatioService;
import com.hp.sh.expv3.bb.component.vo.PosLevelVo;
import com.hp.sh.expv3.bb.constant.RedisKey;
import com.hp.sh.expv3.utils.math.BigUtils;
import com.hp.sh.expv3.utils.math.Precision;

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

    private PosLevelVo findPosLevelVo(Long userId, String asset, String symbol, BigDecimal volume) {
	    HashOperations hashOperations = templateDB0.opsForHash();
	    String hashKey = asset + "__" + symbol;
	    Object s = hashOperations.get(RedisKey.BB_POS_LEVEL, hashKey);
	    List<PosLevelVo> voList = JSON.parseArray(s.toString(), PosLevelVo.class);
	    Optional<PosLevelVo> first = voList.stream().filter(vo -> vo.getMinAmt().compareTo(volume) <= 0 && vo.getMaxAmt().compareTo(volume) >= 0).findFirst();
	    PosLevelVo result = null;
	    for(PosLevelVo vo : voList){
	    	if(BigUtils.between(volume, vo.getMinAmt(), vo.getMaxAmt())){
	    		result = vo;
	    	}
	    }
	    return result;
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

