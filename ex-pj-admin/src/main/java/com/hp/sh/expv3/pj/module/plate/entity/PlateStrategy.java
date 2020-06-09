package com.hp.sh.expv3.pj.module.plate.entity;

import com.hp.sh.expv3.base.entity.BaseBizEntity;

/**
 * 摆盘策略
 * @author wangjg
 */
public class PlateStrategy extends BaseBizEntity {
	private static final long serialVersionUID = 1L;

    // 资产
    private String asset;
    
    // 合约交易品种
    private String symbol;
    
    // 模块
    private String module;
	
	// 摆动（步进）区间(最小)
	private Double stepMin;
	// 摆动（步进）区间(最大)
	private Double stepMax;

	// 最低区间(最小)
	private Double priceMin;
	// 最低区间(最大)
	private Double priceMax;

	// 频率区间(最小)
	private Integer delayMin;
	// 频率区间(最大)
	private Integer delayMax;

	// 数量区间(最小)
	private Double volumeMin;
	//数量区间(最大)
	private Double volumeMax;

	// 备注
	private String remark;
	
	//启用:1-启用,0-禁用
	private Integer enabled;
	
	public Long getId() {
		return id;
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

	public Double getStepMin() {
		return stepMin;
	}

	public void setStepMin(Double stepMin) {
		this.stepMin = stepMin;
	}

	public Double getStepMax() {
		return stepMax;
	}

	public void setStepMax(Double stepMax) {
		this.stepMax = stepMax;
	}

	public Double getPriceMin() {
		return priceMin;
	}

	public void setPriceMin(Double priceMin) {
		this.priceMin = priceMin;
	}

	public Double getPriceMax() {
		return priceMax;
	}

	public void setPriceMax(Double priceMax) {
		this.priceMax = priceMax;
	}

	public Integer getDelayMin() {
		return delayMin;
	}

	public void setDelayMin(Integer delayMin) {
		this.delayMin = delayMin;
	}

	public Integer getDelayMax() {
		return delayMax;
	}

	public void setDelayMax(Integer delayMax) {
		this.delayMax = delayMax;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Double getVolumeMin() {
		return volumeMin;
	}

	public void setVolumeMin(Double volumeMin) {
		this.volumeMin = volumeMin;
	}

	public Double getVolumeMax() {
		return volumeMax;
	}

	public void setVolumeMax(Double volumeMax) {
		this.volumeMax = volumeMax;
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

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	@Override
	public String toString() {
		return "PlateStrategy [asset=" + asset + ", symbol=" + symbol + ", module=" + module + ", stepMin=" + stepMin
				+ ", stepMax=" + stepMax + ", priceMin=" + priceMin + ", priceMax=" + priceMax + ", delayMin="
				+ delayMin + ", delayMax=" + delayMax + ", volumeMin=" + volumeMin + ", volumeMax=" + volumeMax
				+ ", remark=" + remark + ", enabled=" + enabled + "]";
	}

}
