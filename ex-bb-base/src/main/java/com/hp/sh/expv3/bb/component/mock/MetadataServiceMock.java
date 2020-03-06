package com.hp.sh.expv3.bb.component.mock;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.component.MetadataService;
import com.hp.sh.expv3.bb.component.vo.BBSymbolVO;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Component
public class MetadataServiceMock implements MetadataService {

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
        bb1.setAsset("USDT");
        bb1.setSymbol("BTC_USDT");
        bb1.setBbGroup(1);
        list.add(bb1);

        BBSymbolVO bb2 = new BBSymbolVO();
        bb2.setAsset("USDT");
        bb2.setSymbol("ETH_USDT");
        bb2.setBbGroup(1);
        list.add(bb2);
        
        return list;
    }

}
