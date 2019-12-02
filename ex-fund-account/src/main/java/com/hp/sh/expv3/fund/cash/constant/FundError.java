package com.hp.sh.expv3.fund.cash.constant;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 资金错误
 * 项目{2}|模块{2}|错误码{2}
 * 
 * @author wangjg
 *
 */
public class FundError extends ErrorCode {
	
	public static final FundError NOT_ENOUGH = new FundError(010101, "余额不足！");
	
	//重复提交
	public static final FundError REPEAT_ORDER = new FundError(010102, "记录有已存在！");
	
	private FundError(int code, String message) {
		super(code, message);
	}

}
