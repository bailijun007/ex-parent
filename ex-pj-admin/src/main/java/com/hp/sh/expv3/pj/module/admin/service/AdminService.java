
package com.hp.sh.expv3.pj.module.admin.service;

import java.util.List;

import com.hp.sh.expv3.pj.module.admin.entity.Admin;

/**
 * 
 * @author wangjg
 *
 */
public interface AdminService {

	public Admin getAdmin(Long id);

	public void save(Admin admin);

	public void batchSave(List<Admin> list);

	public void update(Admin admin);

	public void delete(Long id);

	public Admin findByUsername(String username);

}
