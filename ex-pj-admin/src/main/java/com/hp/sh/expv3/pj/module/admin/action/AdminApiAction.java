package com.hp.sh.expv3.pj.module.admin.action;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.base.exceptions.BizException;
import com.gitee.hupadev.base.exceptions.CommonError;
import com.gitee.hupadev.base.web.WebUtils;
import com.hp.sh.expv3.pj.constant.UserError;
import com.hp.sh.expv3.pj.module.admin.action.request.ChangePasswd;
import com.hp.sh.expv3.pj.module.admin.action.request.LoginRequest;
import com.hp.sh.expv3.pj.module.admin.constant.Authority;
import com.hp.sh.expv3.pj.module.admin.constant.Resources;
import com.hp.sh.expv3.pj.module.admin.entity.Admin;
import com.hp.sh.expv3.pj.module.admin.vo.MenuItem;
import com.hp.sh.expv3.pj.utils.PasswordUtils;

import io.swagger.annotations.ApiOperation;

/**
 * 登陆
 * 
 * @author wangjg
 */
@RestController
@RequestMapping("/admin/api/passport")
public class AdminApiAction extends AdminBaseAction {
	private static final Logger logger = LoggerFactory.getLogger(AdminApiAction.class);
	
	@ApiOperation(value = "手机号登陆", httpMethod = "POST")
	@RequestMapping("/login")
	public String login(HttpServletResponse response, @RequestBody LoginRequest request) {
		if(StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getPassword())){
			throw new BizException(CommonError.PARAM_ERROR);
		}
		
        Admin admin = adminService.findByUsername(request.getUsername());

        if(admin==null){
        	throw new BizException(UserError.USER_NOT_EXIST);
        }
        if(!request.pwHex().equals(admin.getPasswd())){
        	throw new BizException(UserError.WRONG_PW);
        }
        
        this.saveSession(admin, response);
        return admin.getUsername();
	}
	
	private String saveSession(Admin user, HttpServletResponse response) {
		String sid = RandomStringUtils.randomAlphabetic(64);
		this.cache.put(userCacheKey(sid), user.getId(), 3600*24*30);
		
		Cookie cookie = new Cookie("X-TOKEN", sid);
		cookie.setPath("/");
		response.addCookie(cookie);
		
		return sid;
	}

	/**
	 * 这里springmvc有一个莫名其秒的坑：如果传入参数response，返回值是void就返回空白，不经过BodyAdvice。如果return null，也是如此。
	 * @param response
	 * @return
	 */
	@RequestMapping("/logout")
	public String logout(HttpServletResponse response) {
		this.cache.del(userCacheKey(this.getXToken()));
		WebUtils.removeCookie(response, "X-TOKEN");
		return "";
	}

	@ApiOperation(value = "通过旧密码修改密码", httpMethod = "POST")
	@RequestMapping("/changePasswd1")
	public void changepw(@RequestBody ChangePasswd request) {
		
		PasswordUtils.checkPwFormat(request.getNewPassword());
		
		Long adminId = this.currentUserId();
		Admin admin = this.adminService.getAdmin(adminId);
		if(!admin.getPasswd().equals(request.oldPwHex())){
			throw new BizException(UserError.WRONG_PW);
		}
		admin.setPasswd(request.newPwHex());
		this.adminService.update(admin);
	}


	@RequestMapping("/menu.json")
	public List<MenuItem> menu() {
		Admin admin = this.loginAdmin();
		List<MenuItem> menuItems = Resources.getMenuItems();
		Iterator<MenuItem> it = menuItems.iterator();
		while(it.hasNext()){
			MenuItem item = it.next();
			if(!Authority.check(admin.getType(), item.getAuthority())){
				it.remove();
			}
		}
		return menuItems;
	}
	
}
