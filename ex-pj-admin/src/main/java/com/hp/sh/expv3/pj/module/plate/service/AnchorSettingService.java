
package com.hp.sh.expv3.pj.module.plate.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.pj.module.plate.dao.AnchorSettingDAO;
import com.hp.sh.expv3.pj.module.plate.entity.AnchorSetting;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class AnchorSettingService{

	@Autowired
	private AnchorSettingDAO anchorSettingDAO;

	public AnchorSetting getAnchorSetting(Long id ) {
		return this.anchorSettingDAO.findById(id);
	}

	public void save(AnchorSetting anchorSetting){
		Long now = DbDateUtils.now();
		if(anchorSetting.getId()==null){
			anchorSetting.setCreated(now);
			anchorSetting.setModified(now);
			this.anchorSettingDAO.save(anchorSetting);
		}else{
			anchorSetting.setModified(now);
			this.anchorSettingDAO.update(anchorSetting);
		}
	}

	public void update(AnchorSetting anchorSetting){
		Long now = DbDateUtils.now();
		anchorSetting.setModified(now);
		this.anchorSettingDAO.update(anchorSetting);
	}

	public List<AnchorSetting> pageQuery(Page page) {
		return this.anchorSettingDAO.queryList(Collections.emptyMap());
	}

	public AnchorSetting findBySymbol(String asset, String symbol) {
		AnchorSetting anchorSetting = this.anchorSettingDAO.findBySymbol(asset, symbol);
		return anchorSetting;
	}

}
