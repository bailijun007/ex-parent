/**
 * 
 */
package com.hp.sh.expv3.pj.module.admin.action.response;

import java.util.Map;

import com.gitee.hupadev.base.api.PageResult2;

/**
 * @author wangjg
 */
public class ReportPageResult extends PageResult2 {
	
	private Map<Object, Object> userMap;

	public Map<Object, Object> getUserMap() {
		return userMap;
	}

	public void setUserMap(Map<Object, Object> userMap) {
		this.userMap = userMap;
	}

}
