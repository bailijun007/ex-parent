package com.hp.sh.expv3.pj.module.plate.entity;

import java.math.BigDecimal;

import javax.persistence.Transient;

import com.hp.sh.expv3.base.entity.BaseBizEntity;

/**
 * 锚定价格设置
 * @author wangjg
 */
public class AnchorSetting extends BaseBizEntity{
	private static final long serialVersionUID = 1L;

    // 资产
    private String asset;
    
    // 交易对
    private String symbol;
    
    // 锚定资产
    private String anchorAsset;
    
    // 锚定交易对
    private String anchorSymbol;

	//期望价格
	private BigDecimal expectedPrice;
	
	//锚定价格（设置时的价格）
	private BigDecimal anchorPrice;

	//锚定比例
	private Double anchorRatio;
	
	//最新锚定价(未溢价)
	@Transient
	private BigDecimal newestAnchorPrice;
	
	//最新锚定价(溢价后)
	@Transient
	private BigDecimal newestPrice;
	
	// 摆动（步进）区间(最小)
	private Double swingMin;
	// 摆动（步进）区间(最大)
	private Double swingMax;
	
	public Long getId() {
		return id;
	}

	public AnchorSetting() {
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

	public String getAnchorAsset() {
		return anchorAsset;
	}

	public void setAnchorAsset(String anchorAsset) {
		this.anchorAsset = anchorAsset;
	}

	public String getAnchorSymbol() {
		return anchorSymbol;
	}

	public void setAnchorSymbol(String anchorSymbol) {
		this.anchorSymbol = anchorSymbol;
	}

	public BigDecimal getExpectedPrice() {
		return expectedPrice;
	}

	public void setExpectedPrice(BigDecimal expectedPrice) {
		this.expectedPrice = expectedPrice;
	}

	public BigDecimal getAnchorPrice() {
		return anchorPrice;
	}

	public void setAnchorPrice(BigDecimal anchorPrice) {
		this.anchorPrice = anchorPrice;
	}

	public Double getAnchorRatio() {
		return anchorRatio;
	}

	public void setAnchorRatio(Double anchorRatio) {
		this.anchorRatio = anchorRatio;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getNewestAnchorPrice() {
		return newestAnchorPrice;
	}

	public void setNewestAnchorPrice(BigDecimal newestAnchorPrice) {
		this.newestAnchorPrice = newestAnchorPrice;
	}

	public BigDecimal getNewestPrice() {
		return newestPrice;
	}

	public void setNewestPrice(BigDecimal newestPrice) {
		this.newestPrice = newestPrice;
	}

	public Double getSwingMin() {
		return swingMin;
	}

	public void setSwingMin(Double swingMin) {
		this.swingMin = swingMin;
	}

	public Double getSwingMax() {
		return swingMax;
	}

	public void setSwingMax(Double swingMax) {
		this.swingMax = swingMax;
	}

	@Override
	public String toString() {
		return "AnchorSetting [asset=" + asset + ", symbol=" + symbol + ", anchorAsset=" + anchorAsset
				+ ", anchorSymbol=" + anchorSymbol + ", expectedPrice=" + expectedPrice + ", anchorPrice=" + anchorPrice
				+ ", anchorRatio=" + anchorRatio + ", newestAnchorPrice=" + newestAnchorPrice + ", newestPrice="
				+ newestPrice + ", swingMin=" + swingMin + ", swingMax=" + swingMax + "]";
	}

}
