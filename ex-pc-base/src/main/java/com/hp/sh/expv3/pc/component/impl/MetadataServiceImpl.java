package com.hp.sh.expv3.pc.component.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.component.vo.PcContractVO;
import com.hp.sh.expv3.pc.constant.RedisKey;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Primary
@Component
public class MetadataServiceImpl implements MetadataService {

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
        PcContractVO vo = this.getPcContract(asset, symbol);
        Optional<PcContractVO> optional = Optional.ofNullable(vo);
        BigDecimal decimal = optional.map(p -> p.getFaceValue()).orElse(BigDecimal.ZERO);
        return decimal;
    }

    @Override
	public PcContractVO getPcContract(String asset, String symbol) {
        HashOperations hashOperations = templateDB0.opsForHash();
        String hashKey = asset+"__"+symbol;
        Object o = hashOperations.get(RedisKey.PC_CONTRACT, hashKey);
        String json = o.toString();
        PcContractVO vo = JSON.parseObject(json, PcContractVO.class);
        return vo;
    }

    public List<PcContractVO> getAllPcContract(){
    	//TODO
    	return null;
    }


}