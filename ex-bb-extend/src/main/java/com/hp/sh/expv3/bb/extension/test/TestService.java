package com.hp.sh.expv3.bb.extension.test;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.service.BbAccountRecordExtService;
import com.hp.sh.expv3.bb.extension.service.BbOrderExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import com.hp.sh.expv3.bb.extension.vo.BbHistoryOrderVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/5/9
 */
@ActiveProfiles("bai")
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestService {
    @Autowired
    private BbAccountRecordExtService bbAccountRecordExtService;

    @Autowired
    private BbOrderExtService bbOrderExtService;

    @Test
    public void queryHistory(){
         PageResult<BbAccountRecordVo> pageResult = bbAccountRecordExtService.queryHistory(null, "btc", null, null, 1, 20);

         if(null!=pageResult){
             System.out.println("pageResult.getList()="+pageResult.getList());
         }
    }

    @Test
    public void query(){
//        final List<BbAccountRecordExtVo> extVos = bbAccountRecordExtService.listBbAccountRecords(null, "btc", 2, 0, 1586241240000L, 1588953600000L, 20);
//        System.out.println("extVos = " + extVos);

        final List<BbAccountRecordExtVo> extVos = bbAccountRecordExtService.listBbAccountRecordsByPage(null, "btc", 2, 0, 2L, -1, 1586241240000L, 1588953600000L, 20);
        System.out.println("extVos = " + extVos);
    }

    @Test
    public void queryAllBbOrederHistory(){
        final PageResult<BbOrderVo> pageResult = bbOrderExtService.queryAllBbOrederHistory(null, "USDT", "BTC_USDT", null, null, 1, 2);
        if (!CollectionUtils.isEmpty(pageResult.getList())){
            System.out.println("pageResult.getList()="+pageResult.getList());
        }
    }


    @Test
    public void testqueryHistoryOrderList(){
        final PageResult<BbHistoryOrderVo> result = bbOrderExtService.queryHistoryOrderList(null, "USDT", "BTC_USDT", 0, 20, null, 1, 1590940800000L, 1591113600000L);
        System.out.println("result.getList()="+result.getList());
    }

    @Test
    public void testqueryOrderList(){
        List<Integer> statusList=new ArrayList<>();
        statusList.add(8);
        List<String> assetList=new ArrayList<>();
        assetList.add("USDT");
        List<String> symbolList=new ArrayList<>();
        symbolList.add("BTC_USDT");
        final List<BbHistoryOrderVo> bbHistoryOrderVos = bbOrderExtService.queryOrderList(null, assetList, symbolList, null, null, 20, statusList, null, null);
        System.out.println("bbHistoryOrderVos = " + bbHistoryOrderVos);
    }

    @Test
    public void testqueryBbActiveOrderList(){
        final PageResult<BbHistoryOrderVo> result = bbOrderExtService.queryBbActiveOrderList(null, "USDT", "BTC_USDT", null, 20, null, 1);
        System.out.println("result.getList()="+result.getList());
    }

    @Test
    public void queryHistoryOrderList(){
        final PageResult<BbHistoryOrderVo> result = bbOrderExtService.queryHistoryOrderList(null, "USDT", "BTC_USDT", 1, 2, 1L, -1, null, null);
        System.out.println("result.getList()="+result.getList());
    }

}


