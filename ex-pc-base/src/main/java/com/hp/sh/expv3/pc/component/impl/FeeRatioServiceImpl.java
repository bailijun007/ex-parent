package com.hp.sh.expv3.pc.component.impl;

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
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.component.vo.PosLevelVo;
import com.hp.sh.expv3.pc.constant.RedisKey;
import com.hp.sh.expv3.utils.math.BigUtils;
import com.hp.sh.expv3.utils.math.Precision;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Primary
@Component
public class FeeRatioServiceImpl implements FeeRatioService {
	private static final Logger logger = LoggerFactory.getLogger(FeeRatioServiceImpl.class);

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Resource(name = "templateDB5")
    private StringRedisTemplate templateDB5;



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
    public BigDecimal getTakerFeeRatio(long userId, String asset, String symbol) {
        return findFeeRatio(userId, RedisKey.PC_FEE, RedisKey.KEY_PREFIX_TAKER,templateDB0);
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
	public BigDecimal getMakerFeeRatio(long userId, String asset, String symbol) {
	    return findFeeRatio(userId, RedisKey.PC_FEE, RedisKey.KEY_PREFIX_MAKER,templateDB0);
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
        HashOperations hashOperations = templateDB0.opsForHash();
        String hashKey = asset + "__" + symbol;
        Object s = hashOperations.get(RedisKey.PC_POS_LEVEL, hashKey);
        if (null != s) {
        	try{
	            List<PosLevelVo> voList = JSON.parseArray(s.toString(), PosLevelVo.class);
	            Optional<BigDecimal> first = voList.stream().filter(vo -> vo.getMinAmt().compareTo(volume) <= 0 && vo.getMaxAmt().compareTo(volume) >= 0)
	                    .map(PosLevelVo::getMinHoldMarginRatio).findFirst();
	            return first.orElse(BigDecimal.ZERO);
        	}catch(Exception e){
        		logger.error("获取保证金率失败：{}, {},{},{}", userId, asset, symbol, volume, e);
        		throw new RuntimeException(e);
        	}
        }

        return BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal getMaxLeverage(Long userId, String asset, String symbol, BigDecimal posVolume){
    	PosLevelVo vo = this.findPosLevelVo(userId, asset, symbol, posVolume);
    	if(vo==null){
    		logger.error("{}__{}:{} is null", asset, symbol, posVolume);
    	}
    	return vo.getMaxLeverage();
    }
    
    private PosLevelVo findPosLevelVo(Long userId, String asset, String symbol, BigDecimal volume) {
        HashOperations hashOperations = templateDB0.opsForHash();
        String hashKey = asset + "__" + symbol;
        Object s = hashOperations.get(RedisKey.PC_POS_LEVEL, hashKey);
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
        Object pcFee = hashOperations.get(key, prefix + userId);
        if (pcFee == null) {
        	logger.error("FeeRatio:{}__{}{} is null", key, prefix, userId);
            throw new RuntimeException("获取手续费率失败:"+key+"#"+prefix+userId);
        }
        return new BigDecimal(pcFee + "");
    }

}

