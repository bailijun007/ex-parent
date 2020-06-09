
package com.hp.sh.expv3.pj.module.sys.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pj.module.sys.entity.SysSetting;
import com.hp.sh.expv3.pj.module.sys.mapper.SysSettingDAO;
import com.hp.sh.expv3.pj.module.sys.service.SysSettingService;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class SysSettingServiceImpl implements SysSettingService{

	@Autowired
	private SysSettingDAO sysSettingDAO;
	
	@Override
	@Cacheable(cacheNames="SysSetting", key="#name", unless="#result==null")
	public SysSetting findByName(String name) {
		Map<String, Object> params = this.conMap("name", name);
		SysSetting settting = this.sysSettingDAO.queryOne(params );
		
		return settting;
	}
	
	@Override
	public List<SysSetting> findAll(){
		List<SysSetting> list = this.sysSettingDAO.queryList(Collections.emptyMap());
		return list;
	}
	
	@Override
	public List<SysSetting> findAdminList(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hidden", 0);
		params.put("permission", "admin");
		List<SysSetting> list = this.sysSettingDAO.queryList(params);
		return list;
	}
	
	@Override
	public List<SysSetting> findRootList(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hidden", 0);
		params.put("permission", "root");
		List<SysSetting> list = this.sysSettingDAO.queryList(params);
		return list;
	}

	public SysSetting getSysSetting(Long id ) {
		return this.sysSettingDAO.findById(id);
	}

	@Override
	public void save(SysSetting sysSetting){
		Long now = DbDateUtils.now();
		sysSetting.setCreated(now);
		sysSetting.setModified(now);
		this.sysSettingDAO.save(sysSetting);
	}

	@Override
	@CacheEvict(cacheNames="SysSetting", key="#setting.name")
	public void update(SysSetting setting){
		Long now = DbDateUtils.now();
		setting.setModified(now);
		this.sysSettingDAO.update(setting);
	}

	private Map<String, Object> conMap(String name, Object value){
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(name, value);
		return params;
	}

}
