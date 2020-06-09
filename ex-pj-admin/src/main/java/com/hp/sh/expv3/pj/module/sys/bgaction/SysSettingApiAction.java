
package com.hp.sh.expv3.pj.module.sys.bgaction;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.base.exceptions.BizException;
import com.gitee.hupadev.base.exceptions.CommonError;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.commons.bean.PageResult;
import com.hp.sh.expv3.pj.module.admin.action.AdminBaseAction;
import com.hp.sh.expv3.pj.module.sys.constant.SysConfigKey;
import com.hp.sh.expv3.pj.module.sys.entity.SysSetting;
import com.hp.sh.expv3.pj.module.sys.service.SysSettingService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 系统设置接口
 * @author wangjg
 */
@RestController
@RequestMapping("/admin/syssetting")
public class SysSettingApiAction extends AdminBaseAction{
	@Autowired
	private SysSettingService sysSettingService;

	@ApiOperation(value = "系统设置列表", httpMethod = "POST")
	@RequestMapping("/list")
	public PageResult list(){
		List<SysSetting> list = sysSettingService.findAdminList();
		PageResult pr = this.getPageResult(new Page(1, 50),  list);
		return pr;
	}

	@ApiOperation(value = "系统设置列表", httpMethod = "POST")
	@RequestMapping("/list2")
	public PageResult list2(){
		List<SysSetting> list = sysSettingService.findRootList();
		PageResult pr = this.getPageResult(new Page(1, 50),  list);
		return pr;
	}

	/**
	 * 获取系统设置
	 * @param id 编号
	 * @return
	 */
	@ApiOperation(value = "获取系统设置", httpMethod = "GET")
	@ApiParam(name="id", value="编号", required=true, type="path")
	@RequestMapping("/get")
	public SysSetting get(Long id){
		SysSetting sysSetting = sysSettingService.getSysSetting(id);
		this.checkOwner(sysSetting);
		return sysSetting;
	}

	@ApiOperation(value = "保存系统设置", httpMethod = "POST")
	@RequestMapping("/modify")
	public void modify(@RequestBody SysSetting setting){
		SysSetting setting1 = this.sysSettingService.findByName(setting.getName());
		
		if("root".equals(setting1.getPermission())){
			if(!"root".equals(this.loginAdmin().getUsername())){
				throw new BizException(CommonError.AUTH);
			}
			this.cache.del(SysConfigKey.SYSTEM_ENABLED);
		}
		
		setting1.setValue(setting.getValue());
		this.sysSettingService.update(setting1);
	}

}
