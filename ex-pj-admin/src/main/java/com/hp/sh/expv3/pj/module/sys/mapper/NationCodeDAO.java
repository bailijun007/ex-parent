
package com.hp.sh.expv3.pj.module.sys.mapper;

import java.util.List;
import java.util.Map;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.pj.base.BaseMapper;
import com.hp.sh.expv3.pj.module.sys.entity.NationCode;

/**
 * 
 * @author wangjg
 *
 */
public interface NationCodeDAO extends BaseMapper<NationCode, Long> {

	public List<NationCode> pageQuery(Page page, String orderBy, Boolean asc);

	public List<NationCode> queryList(Map<String, Object> params);

}
