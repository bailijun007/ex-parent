package com.hp.sh.expv3.bb.component.mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.component.MetadataService;
import com.hp.sh.expv3.bb.component.vo.BBSymbolVO;
import com.hp.sh.expv3.bb.constant.RedisKey;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Primary
@Component
public class MetadataServiceMock implements MetadataService {

    @Override
    public BigDecimal getFaceValue(String asset, String symbol) {
        return BigDecimal.ONE;
    }

    @Override
	public BBSymbolVO getBBContract(String asset, String symbol) {
        BBSymbolVO vo = new BBSymbolVO();
        vo.setAsset(asset);
        vo.setSymbol(symbol);
        return vo;
    }

    @Override
    public List<BBSymbolVO> getAllBBContract(){
        List<BBSymbolVO> list = new ArrayList<>();
        
        BBSymbolVO bb1 = new BBSymbolVO();
        bb1.setAsset("USD");
        bb1.setSymbol("BTC_USD");
        bb1.setContractGroup(1);
        list.add(bb1);

        BBSymbolVO bb2 = new BBSymbolVO();
        bb2.setAsset("USD");
        bb2.setSymbol("ETH_USD");
        bb2.setContractGroup(1);
        list.add(bb2);
        
        return list;
    }


}
