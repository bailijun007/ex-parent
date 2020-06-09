
package com.hp.sh.expv3.pj.module.sys.service;

import java.util.List;

import com.hp.sh.expv3.pj.module.sys.entity.SysSetting;

/**
 * 
 * @author wangjg
 *
 */
public interface SysSettingService {

	public SysSetting getSysSetting(Long id);

	public void save(SysSetting sysSetting);

	public void update(SysSetting sysSetting);

	List<SysSetting> findAll();

	public SysSetting findByName(String name);

	/**
	 * @return
	 */
	List<SysSetting> findAdminList();

	/**
	 * @return
	 */
	List<SysSetting> findRootList();
}
