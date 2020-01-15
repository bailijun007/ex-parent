package com.hp.sh.expv3.fund.c2c.action;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.component.PLPayService;
import com.hp.sh.expv3.fund.c2c.constants.C2cConst;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.c2c.service.QueryService;
import com.hp.sh.expv3.fund.c2c.service.SellService;
import com.hp.sh.expv3.fund.c2c.util.GenerateOrderNumUtils;
import com.hp.sh.expv3.fund.cash.constant.ApprovalStatus;
import com.hp.sh.expv3.fund.extension.api.C2cOrderExtApi;
import com.hp.sh.expv3.fund.extension.api.FundAccountExtApi;
import com.hp.sh.expv3.fund.extension.error.ExFundError;
import com.hp.sh.expv3.fund.extension.vo.C2cOrderVo;
import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author BaiLiJun  on 2020/1/9
 */
@RestController
public class C2cOrderExtApiAction implements C2cOrderExtApi {

    private static final Logger logger = LoggerFactory.getLogger(C2cOrderExtApiAction.class);


    @Autowired
    private QueryService queryService;

    @Autowired
    private SellService sellService;

    @Autowired
    private PLPayService plPayService;

    @Autowired
    private FundAccountExtApi fundAccountExtApi;

    ReadWriteLock lock = new ReentrantReadWriteLock();

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
            throw new ExException(ExFundError.PARAM_EMPTY);
        }
        return queryService.pageQueryByPayStatus(payStatus, nextPage, pageSize, id, userId);
    }

    /**
     * * @param userId      用户id
     * * @param ratio       USD/CNY 汇率 例如： USD/CNY = 7.0298
     * * @param srcCurrency 支付币种 例如：
     * * @param tarCurrency 兑换币种
     * * @param fabiAmt     法定货币总金额
     * * @param tarVolume   兑换成资产数量
     *
     * @return
     */
    @Override
    public String create(long userId, BigDecimal ratio, String srcCurrency, String tarCurrency, BigDecimal tarVolume, BigDecimal fabiAmt) {
        String url = plPayService.rujin(userId, ratio, srcCurrency, tarCurrency, tarVolume, fabiAmt);
        return url;
    }

    /**
     * 创建c2c体现订单
     *
     * @param userId       用户id
     * @param bank         开户银行
     * @param bankCardName 银行卡收款姓名
     * @param srcAsset     原资产，比如BTC
     * @param srcNum       资产出金数量 比如 1
     * @param tarAsset     兑换资产 比如 CNY
     * @param tarNum       出金金额（法币） 比如：7000
     * @param ratio 兑换比率
     * @return
     */
    @Override
    public String withdrawalOrder(Long userId, String bank, String bankCardName, String srcAsset, BigDecimal srcNum, String tarAsset, BigDecimal tarNum, BigDecimal ratio) {
        //获取资产账户
        CapitalAccountVo account = fundAccountExtApi.getCapitalAccount(userId, srcAsset);
        //检查c2c 被冻结的资产
        BigDecimal c2cLockedVolume = queryService.getLockC2cNumber(userId, srcAsset);
        BigDecimal remain = account.getTotalAssets().subtract(account.getLock()).subtract(c2cLockedVolume).subtract(srcNum);
        if (remain.compareTo(BigDecimal.ZERO) >= 0) {
            lock.writeLock().lock();
            try {
                //生成c2c体现订单(体现状态为审核中)
                C2cOrder c2cOrder = new C2cOrder();
                c2cOrder.setSn(GenerateOrderNumUtils.getOrderNo(userId));
                c2cOrder.setPayCurrency(srcAsset);
                c2cOrder.setExchangeCurrency(tarAsset);
                c2cOrder.setPrice(ratio);
                c2cOrder.setType(C2cConst.C2C_SELL);
                c2cOrder.setPayStatus(C2cConst.C2C_PAY_STATUS_PAY_SUCCESS);
                c2cOrder.setPayStatusDesc(C2cConst.C2C_PAY_STATUS_DESC_WITHDRAWAL);
                c2cOrder.setPayTime(Instant.now().toEpochMilli());
                c2cOrder.setPayFinishTime(Instant.now().toEpochMilli());
                c2cOrder.setSynchStatus(C2cConst.C2C_SYNCH_STATUS_FALSE);
                c2cOrder.setApprovalStatus(ApprovalStatus.IN_AUDIT);
                c2cOrder.setUserId(userId);
                c2cOrder.setCreated(Instant.now().toEpochMilli());
                c2cOrder.setModified(Instant.now().toEpochMilli());
                c2cOrder.setVolume(srcNum);
                c2cOrder.setAmount(ratio.multiply(srcNum));
                sellService.createC2cOut(c2cOrder);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.writeLock().unlock();
            }
            return "success";
        } else {
            throw new ExException(ExFundError.ORDER_NOT_SUFFICIENT_FUNDS);
        }
    }

    @Override
    public String approvalC2cOrder(Long id,Integer auditStatus) {
        C2cOrder c2cOrder = queryService.queryById(id);
       if(null==c2cOrder){
           throw new ExException(ExFundError.ORDER_NOT_FIND);
       }
        C2cOrder order =new C2cOrder();
        order.setPayFinishTime(Instant.now().toEpochMilli());
        order.setApprovalStatus(auditStatus);
        order.setId(id);
        sellService.updateById(order);

        return "success";
    }

    @Override
    public PageResult<C2cOrderVo> queryAllWithdrawalOrder(Long userId, Integer auditStatus, Integer pageNo, Integer pageSize) {
        if (pageSize == null || pageNo == null) {
            throw new ExException(ExFundError.PARAM_EMPTY);
        }
        return queryService.pageQueryByApprovalStatus(auditStatus,pageNo,pageSize,userId);

    }
}
