/**
 * 
 */
package com.hp.sh.expv3.pj.module.admin.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author wangjg
 */
public class MenuItem {

	private String name;
	private String icon;
	private Integer type;
	private String url;
	private List<MenuItem> list;
	@JsonIgnore
	private Integer authority;
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<MenuItem> getList() {
		return list;
	}
	public void setList(List<MenuItem> list) {
		this.list = list;
	}
	public Integer getAuthority() {
		return authority;
	}
	public void setAuthority(Integer role) {
		this.authority = role;
	}
	@Override
	public String toString() {
		return "MenuItem [name=" + name + ", icon=" + icon + ", type=" + type + ", url=" + url + ", list=" + list
				+ ", authority=" + authority + "]";
	}
}
