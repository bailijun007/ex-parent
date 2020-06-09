package com.hp.sh.expv3.pj.base;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import io.swagger.annotations.ApiModelProperty;

@MappedSuperclass
public class BaseBizEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int NO = 0;
	public static final int YES = 1;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	protected Long id;

	// 创建时间
	@ApiModelProperty("创建时间")
	private Long created;
	
	// 修改时间
	@ApiModelProperty(hidden=false)
	private Long modified;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

}
