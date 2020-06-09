/**
 * 
 */
package com.hp.sh.expv3.pj.module.admin.constant;

/**
 * @author wangjg
 */
public class Resource {

	private String url;
	
	private int authority;
	
	public Resource() {
		super();
	}

	public Resource(String url, int authority) {
		this.url = url;
		this.authority = authority;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getAuthority() {
		return authority;
	}

	public void setAuthority(int authority) {
		this.authority = authority;
	}

	@Override
	public String toString() {
		return "Resource [url=" + url + ", authority=" + authority + "]";
	}

}
