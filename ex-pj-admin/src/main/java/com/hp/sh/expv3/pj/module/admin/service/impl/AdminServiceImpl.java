
package com.hp.sh.expv3.pj.module.admin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pj.module.admin.entity.Admin;
import com.hp.sh.expv3.pj.module.admin.mapper.AdminDAO;
import com.hp.sh.expv3.pj.module.admin.service.AdminService;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class AdminServiceImpl implements AdminService{

	@Autowired
	private AdminDAO adminDAO;

	@Override
	public Admin findByUsername(String username) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", username);
		Admin admin = this.adminDAO.queryOne(params);
		return admin;
	}

	@Override
	public Admin getAdmin(Long id ) {
		return this.adminDAO.findById(id);
	}

	@Override
	public void save(Admin admin){
		Long now = DbDateUtils.now();
		if(admin.getId()==null){
			admin.setCreated(now);
			admin.setModified(now);
			this.adminDAO.save(admin);
		}else{
			admin.setModified(now);
			this.adminDAO.update(admin);
		}
	}

	@Override
	public void batchSave(List<Admin> list){
		Long now = DbDateUtils.now();
		for(Admin admin:list){
			if(admin.getId()==null){
				admin.setCreated(now);
				admin.setModified(now);
				this.adminDAO.save(admin);
			}else{
			admin.setModified(now);
			this.adminDAO.update(admin);
			}
		}
	}

	@Override
	public void update(Admin admin){
		Long now = DbDateUtils.now();
		admin.setModified(now);
		this.adminDAO.update(admin);
	}

	@Override
	public void delete(Long id){
		this.adminDAO.delete(id);

	}

}
