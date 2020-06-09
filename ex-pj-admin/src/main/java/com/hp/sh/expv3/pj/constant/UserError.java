package com.hp.sh.expv3.pj.constant;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 错误码
 * 
 * @author wangjg
 *
 */
public class UserError extends ErrorCode {

	//user
	public static final UserError USER_NOT_EXIST = new UserError(6001, "用户不存在!");
	public static final UserError USER_EXIST = new UserError(6002, "用户已存在!");
	public static final UserError WRONG_PW = new UserError(6003, "密码错误");
	
	public static final UserError YOU_ARE_FRIEND = new UserError(6005, "你们已经是好友了!");
	public static final UserError USER_DISABLED = new UserError(6006, "用户已经禁用!");
	
	//input
	public static final UserError BLANK_USERNAME = new UserError(6040, "用户名不能为空");
	public static final UserError USERNAME_LENGTH = new UserError(6041, "用户昵称错误，1-18位之间有效!");
	public static final UserError PW_LENGTH = new UserError(6042, "密码错误，6-12位之间有效!");
	public static final UserError BLANK_MOBILE = new UserError(6043, "手机号不能为空");
	public static final UserError WRONG_MOBILE_FORMAT = new UserError(6044, "手机号格式错误");
	public static final UserError SMS_FREQUENTLY = new UserError(6045, "请稍后再发");
	public static final UserError INVITECODE = new UserError(6046, "邀请码错误");
	public static final UserError REQUEST_GENDER = new UserError(6047, "性别必填");
	public static final UserError FILE_TOO_LARGE = new UserError(6048, "文件太大");
	public static final UserError BLANK_KEYWORD = new UserError(6049, "搜索内容不能为空");
	
	
	private UserError(int code, String message) {
		super(code, message);
	}

}
