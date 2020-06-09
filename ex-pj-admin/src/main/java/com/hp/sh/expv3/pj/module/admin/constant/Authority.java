/**
 * 
 */
package com.hp.sh.expv3.pj.module.admin.constant;

import com.hp.sh.expv3.pj.module.admin.entity.Admin;

/**
 * @author wangjg
 */
public class Authority {
	
	public static final int SUPER = 1;

	public static final int ADMIN = 2;
	
	public static final int OPERATOR = 4;
	
	public static boolean check(int role, int authority){
		int roleAuth = getRoleAuthoritis(role);
		return (roleAuth & authority) > 0;
	}

	public static int getRoleAuthoritis(int role){
		if(role==Admin.ROLE_SUPER){
			return 0xffffffff;
		}
		if(role==Admin.ROLE_ADMIN){
			return ADMIN|OPERATOR;
		}
		if(role==Admin.ROLE_OPERATOR){
			return OPERATOR;
		}
		return 0;
	}
	
}
