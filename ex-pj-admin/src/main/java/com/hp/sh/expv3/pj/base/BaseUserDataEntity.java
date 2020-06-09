package com.hp.sh.expv3.pj.base;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import io.swagger.annotations.ApiModelProperty;

@MappedSuperclass
public class BaseUserDataEntity implements Serializable, UserData {
	private static final long serialVersionUID = 1L;
	
	public static final int NO = 0;
	public static final int YES = 1;

	@ApiModelProperty("ID")
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	//用户ID，数据创建者 @see User#id
	@ApiModelProperty(value="用户ID", hidden=false)
	@Column(nullable=false)
	private Long userId;

	// 创建时间
	@ApiModelProperty("创建时间")
	private Date created;
	
	// 修改时间
	@ApiModelProperty(hidden=false)
	private Date modified;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
