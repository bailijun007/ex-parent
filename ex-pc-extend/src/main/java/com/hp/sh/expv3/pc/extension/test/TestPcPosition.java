package com.hp.sh.expv3.pc.extension.test;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.pc.extension.api.PcPositionExtendApiAction;
import com.hp.sh.expv3.pc.extension.vo.CurrentPositionVo;
import com.hp.sh.expv3.pc.extension.vo.PcSymbolPositionStatVo;
import com.hp.sh.expv3.pc.extension.vo.PcSymbolPositionTotalVo;
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
public class TestPcPosition {
    @Autowired
    PcPositionExtendApiAction pcPositionExtendApiAction;

    //添加开始结束时间
    @Test
    public void findCurrentPosition() {
        final List<CurrentPositionVo> currentPosition = pcPositionExtendApiAction.findCurrentPosition(1L, "BTC", "BTC_USDT", "2020-05-21", "2020-05-22");
        System.out.println("currentPosition = " + currentPosition);
    }

    //asset symbol 改成必填， 添加开始结束时间
    @Test
    public void findPositionList() {
        final PageResult<CurrentPositionVo> positionList = pcPositionExtendApiAction.findPositionList(1L, "BTC", null, null, "BTC_USDT", 1, 20, "2020-05-21", "2020-05-22");
        System.out.println("positionList = " + positionList.getList());
    }

    //没有改变
    @Test
    public void selectPosByAccount(){
        final List<CurrentPositionVo> list = pcPositionExtendApiAction.selectPosByAccount(1L, "BTC", "BTC_USDT", 1589932800000L);
        System.out.println("list = " + list);
    }


    //没有改变
    @Test
    public void getSymbolPositionStat(){
        final List<PcSymbolPositionStatVo> symbolPositionStat = pcPositionExtendApiAction.getSymbolPositionStat("BTC", "BTC_USDT");
        System.out.println("symbolPositionStat = " + symbolPositionStat);
    }
    //没有改变
    @Test
    public void getSymbolPositionTotal(){
        final PcSymbolPositionTotalVo symbolPositionTotal = pcPositionExtendApiAction.getSymbolPositionTotal("BTC", "BTC_USDT");
        System.out.println("symbolPositionTotal = " + symbolPositionTotal);

    }

}
