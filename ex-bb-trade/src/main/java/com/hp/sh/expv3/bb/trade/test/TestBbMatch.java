package com.hp.sh.expv3.bb.trade.test;
import java.math.BigDecimal;

import com.hp.sh.expv3.bb.trade.dao.BbMatchExtMapper;
import com.hp.sh.expv3.bb.trade.pojo.BbMatchExtVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
@SpringBootTest
@ActiveProfiles("bai")
@RunWith(SpringRunner.class)
public class TestBbMatch {

    @Autowired
    private BbMatchExtMapper bbMatchExtMapper;

    @Test
    public void test() {
        List<BbMatchExtVo> list=new ArrayList<>();
         BbMatchExtVo matchExtVo = new BbMatchExtVo();
         matchExtVo.setId(0L);
         matchExtVo.setAsset("USDT");
         matchExtVo.setSymbol("ETC_USDT");
         matchExtVo.setMatchTxId(0L);
         matchExtVo.setTkAccountId(0L);
         matchExtVo.setTkOrderId(0L);
         matchExtVo.setMkAccountId(0L);
         matchExtVo.setMkOrderId(0L);
         matchExtVo.setPrice(new BigDecimal("0"));
         matchExtVo.setNumber(new BigDecimal("0"));
         matchExtVo.setTradeTime(0L);
         matchExtVo.setTkBidFlag(0L);
         matchExtVo.setModified(0L);
         matchExtVo.setCreated(0L);
        list.add(matchExtVo);
        bbMatchExtMapper.batchSave(list);

    }
}
