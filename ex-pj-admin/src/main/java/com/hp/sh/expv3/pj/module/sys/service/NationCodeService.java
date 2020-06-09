
package com.hp.sh.expv3.pj.module.sys.service;

import java.util.List;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.pj.module.sys.entity.NationCode;

/**
 * 
 * @author wangjg
 *
 */
public interface NationCodeService {

	public NationCode getNationCode(Long id);

	public void save(NationCode nationCode);

	public void batchSave(List<NationCode> list);

	public void update(NationCode nationCode);

	public void delete(Long id);

	public void batchDelete(String idseq) ;

	public List<NationCode> pageQuery(Page page, Integer enabled, String keyword);



}
