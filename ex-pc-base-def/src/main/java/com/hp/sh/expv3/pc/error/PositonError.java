package com.hp.sh.expv3.pc.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 错误码
 * 02-01-00
 * @author wangjg
 *
 */
public class PositonError extends ErrorCode {

	public static final PositonError POS_NOT_ENOUGH = new PositonError(020200, "超过了仓位!");
	public static final PositonError HAVE_ACTIVE_ORDER = new PositonError(020201, "当前存在挂单，不能修改杠杆");
	public static final PositonError LIQING = new PositonError(020202, "强平中。。。");
	public static final PositonError NO_MORE_MARGIN = new PositonError(020203, "没有可减少的保证金");

	private PositonError(int code, String message) {
		super(code, message);
	}

}
