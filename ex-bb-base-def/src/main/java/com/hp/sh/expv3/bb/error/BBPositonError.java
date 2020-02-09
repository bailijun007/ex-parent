package com.hp.sh.expv3.bb.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 币币异常模块
 * -13001 ~ -13999 见： bb_exception
 * 仓位：-13101 ~ -13199
 * @author wangjg
 *
 */
public class BBPositonError extends ErrorCode {

	public static final BBPositonError POS_NOT_ENOUGH = new BBPositonError(-13101, "超过了可平仓位!");
	public static final BBPositonError HAVE_ACTIVE_ORDER = new BBPositonError(-13102, "当前存在挂单，不能修改杠杆");
	public static final BBPositonError NO_MORE_MARGIN = new BBPositonError(-13103, "超过了最多仓位可减少保证金");
	
	public static final BBPositonError PARAM_GT_MAX_LEVERAGE = new BBPositonError(-13104, "超过最大杠杆!");

	public static final BBPositonError LIQING = new BBPositonError(-13105, "强平中。。。");
	public static final BBPositonError FORCE_CLOSE = new BBPositonError(-13106, "仓位已强平!");

	private BBPositonError(int code, String message) {
		super(code, message);
	}

}
