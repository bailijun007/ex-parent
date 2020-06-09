
package com.hp.sh.expv3.pj.module.plate.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.pj.module.plate.dao.PlateStrategyDAO;
import com.hp.sh.expv3.pj.module.plate.entity.PlateStrategy;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PlateStrategyService{

	@Autowired
	private PlateStrategyDAO plateStrategyDAO;
	
	public PlateStrategy getPlateStrategy(Long id) {
		return this.plateStrategyDAO.findById(id);
	}

	public void save(PlateStrategy plateStrategy){
		Long now = DbDateUtils.now();
		plateStrategy.setCreated(now);
		plateStrategy.setModified(now);
		this.plateStrategyDAO.save(plateStrategy);
	}

	public PlateStrategy update(PlateStrategy plateStrategy){
		Long now = DbDateUtils.now();
		plateStrategy.setModified(now);
		this.plateStrategyDAO.update(plateStrategy);
		return plateStrategy;
	}

	public List<PlateStrategy> pageQuery(Page page) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("page", page);
		List<PlateStrategy> list = this.plateStrategyDAO.queryList(params);
		return list;
	}

	public PlateStrategy findBySymbol(String asset, String symbol) {
		PlateStrategy ps = this.plateStrategyDAO.findBySymbol(asset, symbol);
		return ps;
	}


}
