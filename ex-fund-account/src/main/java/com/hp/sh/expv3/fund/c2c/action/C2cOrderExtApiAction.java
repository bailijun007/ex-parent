package com.hp.sh.expv3.fund.c2c.action;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.service.QueryService;
import com.hp.sh.expv3.fund.extension.api.C2cOrderExtApi;
import com.hp.sh.expv3.fund.extension.error.FundCommonError;
import com.hp.sh.expv3.fund.extension.vo.C2cOrderVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiLiJun  on 2020/1/9
 */
@RestController
public class C2cOrderExtApiAction implements C2cOrderExtApi {

    @Autowired
    private QueryService queryService;

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
    public PageResult<C2cOrderVo> pageQueryByPayStatus(Integer payStatus, Integer nextPage, Integer pageSize, Long id) {
        if (pageSize == null || nextPage == null || pageSize == null) {
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }
        return queryService.pageQueryByPayStatus(payStatus, nextPage, pageSize, id);
    }
}
