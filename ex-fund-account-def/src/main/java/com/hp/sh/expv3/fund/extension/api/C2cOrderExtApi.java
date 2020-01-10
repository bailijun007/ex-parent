package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.gitee.hupadev.base.api.ResultEntity;
import com.hp.sh.expv3.fund.extension.vo.C2cOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/1/9
 */
@Api(tags = "c2c订单Api")
@FeignClient(value = "ex-fund-account")
public interface C2cOrderExtApi {


    @ApiOperation("通过支付状态分页查询c2c订单，不传则查全部")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键编号", example = "1", required = false),
            @ApiImplicitParam(name = "payStatus", value = "支付状态:0-待支付，1-支付成功，2-支付失败,3:已取消, 4-同步余额, 5-审核中, 6-审核通过", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", example = "0", required = true),
            @ApiImplicitParam(name = "nextPage", value = "1:下一页，-1：上一页", example = "1", required = true)
    })
    @GetMapping(value = "/api/extension/c2c/order/pageQueryByPayStatus")
    public PageResult<C2cOrderVo> pageQueryByPayStatus(@RequestParam("payStatus") Integer payStatus, @RequestParam("nextPage") Integer nextPage,
                                                       @RequestParam("pageSize") Integer pageSize, @RequestParam(value = "id", required = false) Long id,
                                                       @RequestParam("userId") Long userId);


    @ApiOperation(value = "创建c2c充值订单")
    @ResultEntity
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "0", required = true),
            @ApiImplicitParam(name = "ratio", value = "USD/CNY 汇率 例如： USD/CNY = 7.0298", example = "7.0298", required = true),
            @ApiImplicitParam(name = "srcCurrency", value = "支付币种", example = "INR", required = true),
            @ApiImplicitParam(name = "tarCurrency", value = "兑换币种", example = "USDT", required = true),
            @ApiImplicitParam(name = "fabiAmt", value = "法定货币总金额", example = "7000", required = true),
            @ApiImplicitParam(name = "tarVolume", value = "兑换成资产数量", example = "975", required = true)
    })
    @GetMapping("/api/extension/c2c/order/deposit/create")
    public String create(@RequestParam("userId") long userId, @RequestParam("ratio") BigDecimal ratio,
                         @RequestParam("srcCurrency") String srcCurrency, @RequestParam("tarCurrency") String tarCurrency,
                         @RequestParam("tarVolume") BigDecimal tarVolume, @RequestParam("fabiAmt") BigDecimal fabiAmt);


    @ApiOperation(value = "创建c2c体现订单")
    @ResultEntity
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "0", required = true),
            @ApiImplicitParam(name = "bank", value = "开户银行", example = "农业银行", required = true),
            @ApiImplicitParam(name = "bankCardName", value = "银行卡收款姓名", example = "张三", required = true),
            @ApiImplicitParam(name = "bankCard", value = "银行卡号", example = "846546468546465456", required = true),
            @ApiImplicitParam(name = "targetAssetNum", value = "资产出金数量", example = "352", required = true),
            @ApiImplicitParam(name = "fabiAmount", value = "出金金额（法币）", example = "3520", required = true),
            @ApiImplicitParam(name = "targetAsset", value = "兑换资产", example = "USDT", required = true),
            @ApiImplicitParam(name = "sourceAsset", value = "原资产", example = "CNY", required = true)
    })
    @GetMapping("/api/extension/c2c/order/withdrawal/withdrawalOrder")
    public String withdrawalOrder(@RequestParam("userId") Long userId,
                                  @RequestParam("bank") String bank, @RequestParam("bankCardName") String bankCardName,
                                  @RequestParam("targetAssetNum") BigDecimal targetAssetNum, @RequestParam("fabiAmount") BigDecimal fabiAmount,
                                  @RequestParam("targetAsset") String targetAsset, @RequestParam("sourceAsset") String sourceAsset);

}
