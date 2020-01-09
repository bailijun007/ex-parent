package com.hp.sh.expv3.fund.c2c.action;

import com.gitee.hupadev.base.api.PageResult;
import com.gitee.hupadev.base.api.ResultEntity;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.component.PLPayService;
import com.hp.sh.expv3.fund.c2c.service.BuyService;
import com.hp.sh.expv3.fund.c2c.service.QueryService;
import com.hp.sh.expv3.fund.extension.api.C2cOrderExtApi;
import com.hp.sh.expv3.fund.extension.error.FundCommonError;
import com.hp.sh.expv3.fund.extension.vo.C2cOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/1/9
 */
@RestController
public class C2cOrderExtApiAction implements C2cOrderExtApi {

    private static final Logger logger = LoggerFactory.getLogger(C2cOrderExtApiAction.class);


    @Autowired
    private QueryService queryService;

    @Autowired
    private PLPayService plPayService;

    /**
     * 通过支付状态分页查询c2c订单，不传则查全部
     *
     * @param payStatus 支付状态:0-待支付，1-支付成功，2-支付失败,3:已取消, 4-同步余额, 5-审核中, 6-审核通过
     * @param nextPage  1:下一页，-1：上一页
     * @param pageSize  页大小
     * @param id        主键id
     * @return
     */
    @Override
    public PageResult<C2cOrderVo> pageQueryByPayStatus(Integer payStatus, Integer nextPage, Integer pageSize, Long id, Long userId) {
        if (pageSize == null || nextPage == null || pageSize == null || userId == null) {
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }
        return queryService.pageQueryByPayStatus(payStatus, nextPage, pageSize, id, userId);
    }

    /**
     *    * @param userId      用户id
     *      * @param ratio       USD/CNY 汇率 例如： USD/CNY = 7.0298
     *      * @param srcCurrency 支付币种 例如：
     *      * @param tarCurrency 兑换币种
     *      * @param fabiAmt     法定货币总金额
     *      * @param tarVolume   兑换成资产数量
     * @return
     */
    @Override
    public String create(long userId, BigDecimal ratio, String srcCurrency, String tarCurrency, BigDecimal tarVolume, BigDecimal fabiAmt) {
        String url = plPayService.rujin(userId, ratio, srcCurrency, tarCurrency, tarVolume, fabiAmt);
        return url;
    }


}
