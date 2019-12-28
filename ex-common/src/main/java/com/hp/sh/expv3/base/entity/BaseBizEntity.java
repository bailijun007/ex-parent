package com.hp.sh.expv3.base.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.hp.sh.expv3.component.id.utils.GeneratorName;

@MappedSuperclass
public class BaseBizEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ID_PROPERTY = "id";
	
	public static final int NO = 0;
	public static final int YES = 1;

	protected Long id;

	// 创建时间
	private Long created;
	// 修改时间
	private Long modified;

	@Id
	@GeneratedValue(generator=GeneratorName.SNOWFLAKE)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		if(id==0 && this.id!=null){
			return;
		}
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
