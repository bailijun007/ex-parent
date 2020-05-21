package com.hp.sh.expv3.pc.extension.test;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.pc.extension.api.PcOrderExtendApiAction;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.util.CommonDateUtils;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import com.hp.sh.expv3.pc.extension.vo.UserOrderVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/5/20
 */
@ActiveProfiles("bai")
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestOrder {
    @Autowired
    private PcOrderExtendApiAction pcOrderExtendApiAction;

    //接口参数asset，symbol 改成了必填，并且添加了开始时间，结束时间
    @Test
    public void queryOrderList() {
        List<UserOrderVo> list = pcOrderExtendApiAction.queryOrderList(1L, "BTC", "BTC_USDT", null, null, 50, "32", "2020-04-20", "2020-05-22");
        System.out.println("list = " + list);
    }

    //接口参数symbol 改成了必填，并且添加了开始时间，结束时间
    @Test
    public void queryUserActivityOrder() {
        PageResult<UserOrderVo> result = pcOrderExtendApiAction.queryUserActivityOrder(1L, "BTC", "BTC_USDT", null, null, null, null, 1, 20, null, 1, "2020-05-20", "2020-05-21");
        System.out.println("result = " + result.getList());
    }

    //添加了开始时间，结束时间
    @Test
    public void queryHistory() {
        final List<UserOrderVo> list = pcOrderExtendApiAction.queryHistory(1L, "BTC", "BTC_USDT", null, null, null, 1, 20, null, 1, "2020-05-20", "2020-05-21");
        System.out.println("list = " + list);
    }

    //添加了开始时间，结束时间
    @Test
    public void queryAll() {
        final PageResult<UserOrderVo> result = pcOrderExtendApiAction.queryAll(1L, "BTC", "BTC_USDT", null, null, null, 1, 20, null, 1, "2020-05-20", "2020-05-21");
        System.out.println("result = " + result);
    }

    @Test
    public void pageQueryOrderList(){
        final PageResult<UserOrderVo> result = pcOrderExtendApiAction.pageQueryOrderList(null, "BTC", "BTC_USDT", null, null, null, 1, 20, "2020-05-20", "2020-05-21");
        System.out.println("result = " + result);

    }

}
