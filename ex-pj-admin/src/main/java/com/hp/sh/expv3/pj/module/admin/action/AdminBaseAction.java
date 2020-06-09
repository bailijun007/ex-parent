package com.hp.sh.expv3.pj.module.admin.action;

import org.springframework.beans.factory.annotation.Autowired;

import com.hp.sh.expv3.pj.base.BaseBizAction;
import com.hp.sh.expv3.pj.module.admin.entity.Admin;
import com.hp.sh.expv3.pj.module.admin.service.AdminService;

public class AdminBaseAction extends BaseBizAction{
	@Autowired
	protected AdminService adminService;

	protected String userCacheKey(String sid){
		return "pj:admin-login::"+sid;
	}

	protected Admin loginAdmin(){
		Long adminId = this.currentUserId();
		Admin admin = adminService.getAdmin(adminId);
		return admin;
	}
	
}
