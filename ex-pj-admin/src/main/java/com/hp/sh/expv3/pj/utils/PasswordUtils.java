/**
 * 
 */
package com.hp.sh.expv3.pj.utils;

import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import com.gitee.hupadev.base.exceptions.BizException;
import com.hp.sh.expv3.pj.constant.UserError;

/**
 * @author wangjg
 */
public class PasswordUtils {
	
	public static final long SOMETIME = 1565167509016L;
	
	public static String passwdHex(String pw) {
		return DigestUtils.md5Hex(pw);
	}
	
	public static void checkMobileFormat(String mobile){
		if(StringUtils.isBlank(mobile)){
			throw new BizException(UserError.BLANK_MOBILE);
		}
		
		if(!(mobile.length()>=5 && mobile.length()<=20)){
			throw new BizException(UserError.WRONG_MOBILE_FORMAT);
		}
		if(!Pattern.matches("\\d+", mobile)){
			throw new BizException(UserError.WRONG_MOBILE_FORMAT);
		}
	}
	
	public static void checkNameFormat(String name){
		if(StringUtils.isBlank(name) || !(name.length()>=1 && name.length()<=18)){
			throw new BizException(UserError.USERNAME_LENGTH);
		}
	}
	
	public static void checkPwFormat(String password){
		if(StringUtils.isBlank(password) || !(password.length()>=6 && password.length()<=12)){
			throw new BizException(UserError.PW_LENGTH);
		}
	}
	
	public static String genInvitationCode(){
		long time = System.currentTimeMillis() -  SOMETIME;
		int rnd = RandomUtils.nextInt(1000);
		long num = time * 1000 + rnd;
		String s = Long.toString(num, Character.MAX_RADIX);
		return s.toUpperCase();
	}
	
	public static String genImNo(){
		long time = System.currentTimeMillis() -  SOMETIME;
		int rnd = RandomUtils.nextInt(10000);
		long num = time * 10000 + rnd;
		String s = Long.toString(num, Character.MAX_RADIX);
		return s.toUpperCase();
	}

}
