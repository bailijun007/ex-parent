package com.hp.sh.expv3.bb.module.log.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hp.sh.expv3.component.id.utils.GeneratorName;

/**
 * 账户日志
 *
 * @author wangjg
 */
@Table(name = "bb_account_log")
public class BBAccountLog {
	
    //Id
    private Long id;

    /**
     * 日志类型
     */
    private Integer type;

    //用户Id
    private Long userId;

    //资产
    private String asset;
    //交易品种
    private String symbol;
    //引用对象Id
    private Long refId;
    //时间
    private Long time;

    public BBAccountLog() {
    }

    public String tags() {
        return this.getType() + "";
    }

	@Id
	@GeneratedValue(generator=GeneratorName.SNOWFLAKE)
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "BBAccountLog{" +
                "type=" + type +
                ", userId=" + userId +
                ", asset='" + asset + '\'' +
                ", symbol='" + symbol + '\'' +
                ", refId=" + refId +
                ", time=" + time +
                '}';
    }
}
