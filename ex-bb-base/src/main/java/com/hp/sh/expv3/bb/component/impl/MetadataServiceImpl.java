package com.hp.sh.expv3.bb.component.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.component.MetadataService;
import com.hp.sh.expv3.bb.component.vo.AssetVO;
import com.hp.sh.expv3.bb.component.vo.BBSymbolVO;
import com.hp.sh.expv3.bb.constant.RedisKey;

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

    @Override
	public BBSymbolVO getBBSymboll(String asset, String symbol) {
        HashOperations hashOperations = templateDB0.opsForHash();
        String hashKey = asset+"__"+symbol;
        Object o = hashOperations.get(RedisKey.BB_SYMBOL, hashKey);
        String json = o.toString();
        BBSymbolVO vo = JSON.parseObject(json, BBSymbolVO.class);
        return vo;
    }

    @Override
    public List<BBSymbolVO> getAllBBSymbol(){
        HashOperations opsForHash = templateDB0.opsForHash();
        Cursor<Map.Entry<String, Object>> curosr = opsForHash.scan(RedisKey.BB_SYMBOL, ScanOptions.NONE);

        List<BBSymbolVO> list = new ArrayList<>();
        while (curosr.hasNext()) {
            Map.Entry<String, Object> entry = curosr.next();
            Object o = entry.getValue();
            BBSymbolVO bBSymbolVO = JSON.parseObject(o.toString(), BBSymbolVO.class);
            list.add(bBSymbolVO);
        }


        return list;
    }

    @Override
	public AssetVO getAsset(String asset) {
        HashOperations hashOperations = templateDB0.opsForHash();
        String hashKey = asset;
        Object o = hashOperations.get(RedisKey.BB_ASSET, hashKey);
        String json = o.toString();
        AssetVO vo = JSON.parseObject(json, AssetVO.class);
        return vo;
    }

}
