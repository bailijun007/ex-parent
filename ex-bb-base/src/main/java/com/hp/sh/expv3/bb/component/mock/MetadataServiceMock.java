package com.hp.sh.expv3.bb.component.mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.component.MetadataService;
import com.hp.sh.expv3.bb.component.vo.AssetVO;
import com.hp.sh.expv3.bb.component.vo.BBSymbolVO;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Component
public class MetadataServiceMock implements MetadataService {

    @Override
	public BBSymbolVO getBBSymboll(String asset, String symbol) {
        BBSymbolVO vo = new BBSymbolVO();
        vo.setAsset(asset);
        vo.setSymbol(symbol);
        return vo;
    }

    @Override
    public List<BBSymbolVO> getAllBBSymbol(){
        List<BBSymbolVO> list = new ArrayList<>();
        
        BBSymbolVO bb1 = new BBSymbolVO();
        bb1.setAsset("USDT");
        bb1.setSymbol("BTC_USDT");
        bb1.setBbGroupId(1);
        list.add(bb1);

        BBSymbolVO bb2 = new BBSymbolVO();
        bb2.setAsset("USDT");
        bb2.setSymbol("ETH_USDT");
        bb2.setBbGroupId(1);
        list.add(bb2);
        
        return list;
    }

	@Override
	public AssetVO getAsset(String asset) {
		AssetVO vo = new AssetVO();
		vo.setWithdrawFee(new BigDecimal("0.00001"));
		return vo;
	}

}
