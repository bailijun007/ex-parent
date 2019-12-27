package com.hp.sh.expv3.base.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseBizEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ID_PROPERTY = "id";
	
	public static final int NO = 0;
	public static final int YES = 1;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	protected Long id;

	// 创建时间
	private Long created;
	// 修改时间
	private Long modified;

	@Id
	@GeneratedValue(strategy = IDENTITY)
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
