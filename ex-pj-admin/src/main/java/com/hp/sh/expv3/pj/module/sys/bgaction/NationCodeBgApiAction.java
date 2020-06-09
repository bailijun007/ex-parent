
package com.hp.sh.expv3.pj.module.sys.bgaction;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.commons.bean.PageResult;
import com.hp.sh.expv3.pj.module.admin.action.AdminBaseAction;
import com.hp.sh.expv3.pj.module.sys.entity.NationCode;
import com.hp.sh.expv3.pj.module.sys.service.NationCodeService;

/**
 * 手机地区码接口
 * @author wangjg
 */
@RestController
@RequestMapping("/admin/nationCode")
public class NationCodeBgApiAction extends AdminBaseAction{
	@Autowired
	private NationCodeService nationCodeService;

	/**
	 * 手机地区码列表
	 * @param keyword 查询关键字
	 */
	@RequestMapping("/list")
	public PageResult<NationCode> list(Integer pageNo, String keyword){
		Page page = this.getPage(pageNo);
		List<NationCode> list = nationCodeService.pageQuery(page, null, keyword);
		return this.getPageResult(page, list);
	}

	/**
	 * 获取手机地区码
	 * @param id 编号
	 * @return
	 */
	@RequestMapping("/get")
	public NationCode get(Long id){
		NationCode nationCode = nationCodeService.getNationCode(id);
		return nationCode;
	}

	/**
	 * 保存手机地区码
	 */
	@RequestMapping("/save")
	public void save(@RequestBody NationCode nationCode){
		if(nationCode.getId()==null){
			nationCode.setEnabled(NationCode.YES);
			this.nationCodeService.save(nationCode);
		}else{
			this.nationCodeService.update(nationCode);
		}
	}

	/**
	 * 启用/禁用手机地区码
	 * @param id 编号
	 * @param enabled 是否启用
	 * @return
	 */
	@RequestMapping("/enable")
	public void enable(String idseq, Integer enabled){
		if(StringUtils.isBlank(idseq)){
			return;
		}
		String[] ss = idseq.split(",");
		for(String s: ss){
			Long id = Long.parseLong(s);
			NationCode nationCode = nationCodeService.getNationCode(id);
			nationCode.setEnabled(enabled);
			this.nationCodeService.update(nationCode);
		}
	}

	/**
	 * 批量删除手机地区码
	 * @param idseq 编号序列(以‘,’号隔开)
	 * @return
	 */
	@RequestMapping("/batchDelete")
	public void batchDelete(String idseq){
		this.nationCodeService.batchDelete(idseq);
	}

}
