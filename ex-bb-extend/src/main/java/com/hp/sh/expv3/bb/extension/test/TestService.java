package com.hp.sh.expv3.bb.extension.test;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.service.BbAccountRecordExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Test
    public void testHistory2(){
         PageResult<BbAccountRecordVo> pageResult = bbAccountRecordExtService.queryHistory(null, "btc", "2019-03", "2020-05", 1, 20);

         if(null!=pageResult){
             System.out.println("pageResult.getList()="+pageResult.getList());
         }
    }

    @Test
    public void testBBAccountRecord(){
//        final List<BbAccountRecordExtVo> extVos = bbAccountRecordExtService.listBbAccountRecords(null, "btc", 1, 0, 1583562840000L, 1588953600000L, 20);
//        System.out.println("extVos = " + extVos);

        final List<BbAccountRecordExtVo> extVos = bbAccountRecordExtService.listBbAccountRecordsByPage(null, "btc", 1, 0, 2L, -1, 1583562840000L, 1588953600000L, 20);
        System.out.println("extVos = " + extVos);
    }

}
