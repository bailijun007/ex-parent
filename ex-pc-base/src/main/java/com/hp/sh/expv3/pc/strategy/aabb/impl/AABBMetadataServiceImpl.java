package com.hp.sh.expv3.pc.strategy.aabb.impl;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.pc.constant.RedisKey;
import com.hp.sh.expv3.pc.strategy.aabb.AABBMetadataService;
import com.hp.sh.expv3.pc.strategy.vo.PcContractVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author BaiLiJun  on 2019/12/18
 */
public class AABBMetadataServiceImpl extends AABBMetadataService {

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Resource(name = "templateDB5")
    private StringRedisTemplate templateDB5;

    /**
     * redis :
     * db: 0
     * redis key:pc_contract
     * hash key: ${asset}__${symbol}
     * 取 faceValue
     *
     * @param asset
     * @param symbol
     * @return
     */
    @Override
    public BigDecimal getFaceValue(String asset, String symbol) {
        HashOperations hashOperations = templateDB0.opsForHash();
        String hashKey = asset+"__"+symbol;
        Object o = hashOperations.get(RedisKey.PC_CONTRACT, hashKey);
        PcContractVO vo = JSON.parseObject(o.toString(), PcContractVO.class);
        Optional<PcContractVO> optional = Optional.ofNullable(vo);
        BigDecimal decimal = optional.map(p -> p.getFaceValue()).orElse(BigDecimal.ZERO);
        return decimal;
    }
}
