package com.hp.sh.expv3.pj.module.externalOrder.action;

import com.hp.sh.expv3.commons.bean.PageResult;
import com.hp.sh.expv3.pj.module.admin.action.AdminBaseAction;
import com.hp.sh.expv3.pj.module.externalOrder.vo.ExternalOrderListDataVo;
import com.hp.sh.expv3.pj.module.userHoldings.action.UserHoldingsApiAction;
import com.hp.sh.expv3.pj.module.userHoldings.vo.UserHoldingsListVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/6/11
 */
@RestController
@RequestMapping("/admin/externalOrder")
public class externalOrderListDataApiAction extends AdminBaseAction {
    private static final Logger logger = LoggerFactory.getLogger(externalOrderListDataApiAction.class);


    @RequestMapping("/list")
    public PageResult<ExternalOrderListDataVo> list(@RequestParam(value = "symbol", required = false) String symbol,
                                                    @RequestParam(value = "beginTime", required = false) String beginTime,
                                                    @RequestParam(value = "endTime", required = false) String endTime,
                                                    @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        logger.info("今入查询用户持有量接口，收到参数为：symbol={},beginTime={},endTime={},pageNo={},pageSize={}", symbol, beginTime, endTime, pageNo, pageSize);
        PageResult<ExternalOrderListDataVo> pageResult=new PageResult<>();
        List<ExternalOrderListDataVo> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ExternalOrderListDataVo listVo = new ExternalOrderListDataVo();
            listVo.setPrice(new BigDecimal(""+(i+1)*(int)(Math.random()*100)));
            listVo.setNumber(new BigDecimal(""+(i+1)*(int)(Math.random()*100)));
            listVo.setOrderType(i%3==0?1:2);
            list.add(listVo);
        }
        List<ExternalOrderListDataVo> voList = list.stream().skip(pageSize * (pageNo - 1)).limit(pageSize).collect(Collectors.toList());
        pageResult.setList(voList);
        Integer rowTotal = list.size();
        pageResult.setPageNo(pageNo);
        pageResult.setRowTotal(Long.parseLong(String.valueOf(rowTotal)));
        pageResult.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return pageResult;
    }
}
