package com.hp.sh.expv3.bb.kline.test;

import com.hp.sh.expv3.bb.kline.service.impl.BbKlineHistoryCalcFromExpServiceImpl;
import com.hp.sh.expv3.bb.kline.service.impl.BbKlineHistoryMergeFromExpServiceImpl;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author BaiLiJun  on 2020/3/12
 */
@SpringBootTest
@ActiveProfiles("bai")
@RunWith(SpringRunner.class)
public class Test {
    @Autowired
   private BbKlineHistoryCalcFromExpServiceImpl bbKlineHistoryCalcFromExpService;

    @Autowired
    private BbKlineHistoryMergeFromExpServiceImpl bbKlineHistoryMergeFromExpService;


    @org.junit.Test
    public void test1(){
        bbKlineHistoryCalcFromExpService.repairKlineFromExp();
    }


    @org.junit.Test
    public void test2(){
        bbKlineHistoryMergeFromExpService.updateKlineByExpHistory();
    }

}
