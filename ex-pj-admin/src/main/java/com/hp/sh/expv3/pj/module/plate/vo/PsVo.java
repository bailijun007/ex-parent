package com.hp.sh.expv3.pj.module.plate.vo;

import com.hp.sh.expv3.pj.module.plate.entity.AnchorSetting;
import com.hp.sh.expv3.pj.module.plate.entity.PlateStrategy;

public class PsVo {
	
	private PlateStrategy ps;
	
	private AnchorSetting as;

	public PsVo() {
	}

	public PsVo(PlateStrategy ps, AnchorSetting as) {
		this.ps = ps;
		this.as = as;
	}

	public PlateStrategy getPs() {
		return ps;
	}

	public void setPs(PlateStrategy plateStrategy) {
		this.ps = plateStrategy;
	}

	public AnchorSetting getAs() {
		return as;
	}

	public void setAs(AnchorSetting anchorSetting) {
		this.as = anchorSetting;
	}
	
}
