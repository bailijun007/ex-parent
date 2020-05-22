package com.hp.sh.expv3.pc.extension.test;

import com.hp.sh.expv3.pc.extension.api.PcOrderTradeExtendApiAction;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeDetailVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeExtendVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/5/21
 */
@ActiveProfiles("bai")
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestOrderTrade {

    @Autowired
    private PcOrderTradeExtendService pcOrderTradeService;

    @Test
    public void queryOrderTrade() {
        List<PcOrderTradeVo> orderTradeVoList = pcOrderTradeService.queryOrderTrade(1L, "BTC", "BTC_USDT", "1", 1589904000000L, 1589990400000L);
        System.out.println("orderTradeVoList = " + orderTradeVoList);
    }

    @Autowired
    private PcOrderTradeExtendApiAction pcOrderTradeExtendApiAction;

    @Test
    public void queryOrderTradeDetail() {
        final List<PcOrderTradeDetailVo> detailVos = pcOrderTradeExtendApiAction.queryOrderTradeDetail(1L, "BTC", "BTC_USDT", "1", "2020-05-20", "2020-05-22");
        System.out.println("detailVos = " + detailVos);
    }

    @Test
    public void queryTradeRecord() {
        final List<PcOrderTradeDetailVo> pcOrderTradeDetailVos = pcOrderTradeExtendApiAction.queryTradeRecord("BTC", "BTC_USDT", null, null, 50, "2020-05-20", "2020-05-22");
        System.out.println("pcOrderTradeDetailVos = " + pcOrderTradeDetailVos);
    }

    @Test
    public void selectLessTimeTrade() {
        final PcOrderTradeDetailVo pcOrderTradeDetailVo = pcOrderTradeExtendApiAction.selectLessTimeTrade("BTC", "BTC_USDT", 1590044421341L);
        System.out.println("pcOrderTradeDetailVo = " + pcOrderTradeDetailVo);
    }

    @Test
    public void selectPcFeeCollectByAccountId() {
        final List<PcOrderTradeDetailVo> pcOrderTradeDetailVos = pcOrderTradeExtendApiAction.selectPcFeeCollectByAccountId("BTC", "BTC_USDT", 1L, 1589856240000L, 1590044421341L);
        System.out.println("pcOrderTradeDetailVos = " + pcOrderTradeDetailVos);
    }

    @Test
    public void selectTradeListByUserId(){
        final List<PcOrderTradeExtendVo> pcOrderTradeExtendVos = pcOrderTradeExtendApiAction.selectTradeListByUserId("BTC", "BTC_USDT", 1L, 1589856240000L, 1590044421341L);
        System.out.println("pcOrderTradeExtendVos = " + pcOrderTradeExtendVos);
    }

}
