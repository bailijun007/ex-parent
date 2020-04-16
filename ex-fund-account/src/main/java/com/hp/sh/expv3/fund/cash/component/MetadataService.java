package com.hp.sh.expv3.fund.cash.component;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.constant.RedisKey;
import com.hp.sh.expv3.fund.cash.component.vo.AssetVO;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Primary
@Component
public class MetadataService {

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Resource(name = "templateDB5")
    private StringRedisTemplate templateDB5;

	public AssetVO getAsset(String asset) {
        HashOperations hashOperations = templateDB0.opsForHash();
        String hashKey = asset;
        Object o = hashOperations.get(RedisKey.BB_ASSET, hashKey);
        String json = o.toString();
        AssetVO vo = JSON.parseObject(json, AssetVO.class);
        return vo;
    }
	
	public BigDecimal getWithdrawFee(String asset) {
        HashOperations hashOperations = templateDB0.opsForHash();
        String hashKey = asset;
        Object o = hashOperations.get(RedisKey.BB_ASSET, hashKey);
        String json = o.toString();
        AssetVO vo = JSON.parseObject(json, AssetVO.class);
        return vo.getWithdrawFee();
    }

}
