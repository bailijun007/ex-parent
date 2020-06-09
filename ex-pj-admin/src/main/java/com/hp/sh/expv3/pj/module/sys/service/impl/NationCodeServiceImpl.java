
package com.hp.sh.expv3.pj.module.sys.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.pj.module.sys.entity.NationCode;
import com.hp.sh.expv3.pj.module.sys.mapper.NationCodeDAO;
import com.hp.sh.expv3.pj.module.sys.service.NationCodeService;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class NationCodeServiceImpl implements NationCodeService{

	@Autowired
	private NationCodeDAO nationCodeDAO;

	@Override
	public NationCode getNationCode(Long id ) {
		return this.nationCodeDAO.findById(id);
	}

	@Override
	public void save(NationCode nationCode){
		Long now = DbDateUtils.now();
		if(nationCode.getId()==null){
			nationCode.setCreated(now);
			nationCode.setModified(now);
			this.nationCodeDAO.save(nationCode);
		}else{
			nationCode.setModified(now);
			this.nationCodeDAO.update(nationCode);
		}
	}

	@Override
	public void batchSave(List<NationCode> list){
		Long now = DbDateUtils.now();
		for(NationCode nationCode:list){
			if(nationCode.getId()==null){
				nationCode.setCreated(now);
				nationCode.setModified(now);
				this.nationCodeDAO.save(nationCode);
			}else{
			nationCode.setModified(now);
			this.nationCodeDAO.update(nationCode);
			}
		}
	}

	@Override
	public void update(NationCode nationCode){
		Long now = DbDateUtils.now();
		nationCode.setModified(now);
		this.nationCodeDAO.update(nationCode);
	}

	@Override
	public void delete(Long id){
		this.nationCodeDAO.delete(id);

	}

	@Override
	public void batchDelete(String idseq) {
		String[] sa = idseq.split(",");
		for(String s:sa){
			Long id = Long.parseLong(s);
			this.nationCodeDAO.delete(id);

		}
	}

	@Override
	public List<NationCode> pageQuery(Page page, Integer enabled, String keyword) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("enabled", enabled);
		params.put("keyword", keyword);
		params.put("orderBy", "en_name");
		params.put("asc", true);
		return this.nationCodeDAO.queryList(params);
	}

}
