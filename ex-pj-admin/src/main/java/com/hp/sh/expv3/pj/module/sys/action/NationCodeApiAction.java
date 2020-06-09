
package com.hp.sh.expv3.pj.module.sys.action;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.hp.sh.expv3.pj.base.BaseBizAction;
import com.hp.sh.expv3.pj.module.sys.action.response.NationCodeVo;
import com.hp.sh.expv3.pj.module.sys.entity.NationCode;
import com.hp.sh.expv3.pj.module.sys.service.NationCodeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 手机地区码接口
 * @author wangjg
 */
@Api(tags="手机地区码接口")
@RestController
@RequestMapping("/public/nationCode")
public class NationCodeApiAction extends BaseBizAction{
	@Autowired
	private NationCodeService nationCodeService;
	
	/**
	 * 获取手机地区码列表
	 * @param keyword
	 * @return
	 */
	@ApiOperation(value = "获取手机地区码列表", httpMethod = "GET")
	@GetMapping("/select")
	public List<NationCodeVo> select(){
		List<NationCode> list = nationCodeService.pageQuery(null, NationCode.YES, null);
		List<NationCodeVo> result = BeanHelper.copyList(list, NationCodeVo.class);
		return result;
	}


}
