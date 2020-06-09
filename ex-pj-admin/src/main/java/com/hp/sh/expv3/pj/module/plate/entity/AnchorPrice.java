package com.hp.sh.expv3.pj.module.plate.entity;

import java.math.BigDecimal;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.hp.sh.expv3.component.id.utils.GeneratorName;

/**
 * 锚定价格
 * @author wangjg
 */
public class AnchorPrice{
	private Long id;

	//价格
	private BigDecimal price;

	private Long created;
	
	//策略id
	private Long psId;

	@Id
	@GeneratedValue(generator=GeneratorName.SNOWFLAKE)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Long getPsId() {
		return psId;
	}

	public void setPsId(Long psId) {
		this.psId = psId;
	}

	@Override
	public String toString() {
		return "AnchorPrice [id=" + id + ", price=" + price + ", created=" + created + ", psId=" + psId + "]";
	}

}
