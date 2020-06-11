package com.hp.sh.expv3.pj.module.userHoldings.action;
import java.math.BigDecimal;

import com.hp.sh.expv3.commons.bean.PageResult;
import com.hp.sh.expv3.pj.module.admin.action.AdminBaseAction;
import com.hp.sh.expv3.pj.module.userHoldings.vo.UserHoldingsListVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 登陆
 *
 * @author wangjg
 */
@RestController
@RequestMapping("/admin/userHoldings")
public class UserHoldingsApiAction extends AdminBaseAction {
    private static final Logger logger = LoggerFactory.getLogger(UserHoldingsApiAction.class);

    //	@ApiOperation(value = "查询用户持有量", httpMethod = "POST")
    @RequestMapping("/list")
    public PageResult<UserHoldingsListVo> list(@RequestParam(value = "symbol", required = false) String symbol,
                                               @RequestParam(value = "beginTime", required = false) String beginTime,
                                               @RequestParam(value = "endTime", required = false) String endTime,
                                               @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                               @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        logger.info("今入查询用户持有量接口，收到参数为：symbol={},beginTime={},endTime={},pageNo={},pageSize={}", symbol, beginTime, endTime, pageNo, pageSize);
        PageResult<UserHoldingsListVo> pageResult=new PageResult<>();
        List<UserHoldingsListVo> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
             UserHoldingsListVo listVo = new UserHoldingsListVo();
             listVo.setSymbol("BYM_USDT");
             listVo.setId((long) (i+1));
             listVo.setUsername("张三"+i+1);
             listVo.setOrderTime(Instant.now().toEpochMilli());
             listVo.setHoldingNumber(new BigDecimal(""+i+20));
             listVo.setOrderType(i%2==0?1:2);
             listVo.setOrderTotal(new BigDecimal(""+(i+1)*22));
             listVo.setAvgTradePrice(new BigDecimal(""+(i+1)*25));
            list.add(listVo);
        }
         List<UserHoldingsListVo> voList = list.stream().skip(pageSize * (pageNo - 1)).limit(pageSize).collect(Collectors.toList());
        pageResult.setList(voList);
        Integer rowTotal = list.size();
        pageResult.setPageNo(pageNo);
        pageResult.setRowTotal(Long.parseLong(String.valueOf(rowTotal)));
        pageResult.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return pageResult;
    }


}
