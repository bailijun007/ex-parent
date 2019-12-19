package com.hp.sh.expv3.pc.strategy.aabb.impl;

import com.hp.sh.expv3.pc.strategy.aabb.AABBMetadataService;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/18
 */
public class AABBMetadataServiceImpl extends AABBMetadataService {
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
        return null;
    }
}
