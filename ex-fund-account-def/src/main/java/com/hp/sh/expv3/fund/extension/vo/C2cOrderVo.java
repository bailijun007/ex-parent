package com.hp.sh.expv3.fund.extension.vo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * c2c订单
 *
 * @author wangjg
 */
public class C2cOrderVo implements Serializable {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;


    @ApiModelProperty("创建时间")
    private Long created;

    @ApiModelProperty("修改时间")
    private Long modified;

    @ApiModelProperty("订单号")
    private String sn;

    //支付币种
    @ApiModelProperty("支付币种")
    private String payCurrency;

    //兑换币种
    @ApiModelProperty("兑换币种")
    private String exchangeCurrency;

    //1 买入，-1 卖出
    @ApiModelProperty("1 买入，-1 卖出")
    private Integer type;

    //价格
    @ApiModelProperty("价格")
    private BigDecimal price;

    //数量
    @ApiModelProperty("数量")
    private BigDecimal volume;

    //总额（回调返回）
    @ApiModelProperty("总额")
    private BigDecimal amount;

    //执行状态:0-待支付，1-支付成功，2-支付失败,3:已取消, 4-同步余额, 5-审核中, 6-审核通过
    @ApiModelProperty("0-待支付，1-支付成功，2-支付失败,3:已取消, 4-同步余额, 5-审核中, 6-审核通过")
    protected Integer payStatus;

    //支付状态描述
    @ApiModelProperty("支付状态描述")
    protected String payStatusDesc;

    //支付时间
    @ApiModelProperty("支付时间")
    protected Long payTime;

    //支付完成时间
    @ApiModelProperty("支付完成时间")
    protected Long payFinishTime;

    //同步状态
    @ApiModelProperty("同步状态")
    protected Integer synchStatus;

    //审批状态
    @ApiModelProperty("审批状态")
    private Integer approvalStatus;


    public C2cOrderVo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    public String getExchangeCurrency() {
        return exchangeCurrency;
    }

    public void setExchangeCurrency(String exchangeCurrency) {
        this.exchangeCurrency = exchangeCurrency;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public String getPayStatusDesc() {
        return payStatusDesc;
    }

    public void setPayStatusDesc(String payStatusDesc) {
        this.payStatusDesc = payStatusDesc;
    }

    public Long getPayTime() {
        return payTime;
    }

    public void setPayTime(Long payTime) {
        this.payTime = payTime;
    }

    public Long getPayFinishTime() {
        return payFinishTime;
    }

    public void setPayFinishTime(Long payFinishTime) {
        this.payFinishTime = payFinishTime;
    }

    public Integer getSynchStatus() {
        return synchStatus;
    }

    public void setSynchStatus(Integer synchStatus) {
        this.synchStatus = synchStatus;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }


}
