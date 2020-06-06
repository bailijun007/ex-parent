package com.hp.sh.expv3.pc.component.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.component.vo.PcContractVO;
import com.hp.sh.expv3.pc.component.vo.PosLevelVo;
import com.hp.sh.expv3.pc.constant.RedisKey;
import com.hp.sh.expv3.utils.math.BigUtils;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Primary
@Component
public class MetadataServiceImpl implements MetadataService {
	private static final Logger logger = LoggerFactory.getLogger(MetadataServiceImpl.class);

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

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

    @Override
    public List<PcContractVO> getAllPcContract(){
        HashOperations opsForHash = templateDB0.opsForHash();
        Cursor<Map.Entry<String, Object>> curosr = opsForHash.scan(RedisKey.PC_CONTRACT, ScanOptions.NONE);

        List<PcContractVO> list = new ArrayList<>();
        while (curosr.hasNext()) {
            Map.Entry<String, Object> entry = curosr.next();
            Object o = entry.getValue();
            PcContractVO pcContractVO = JSON.parseObject(o.toString(), PcContractVO.class);
            list.add(pcContractVO);
        }


        return list;
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

}
