package com.hp.sh.expv3.pc.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 错误码
 * 02-02-00
 * @author wangjg
 *
 */
public class PcPositonError extends ErrorCode {

	public static final PcPositonError POS_NOT_ENOUGH = new PcPositonError(20200, "超过了仓位!");
	public static final PcPositonError HAVE_ACTIVE_ORDER = new PcPositonError(20201, "当前存在挂单，不能修改杠杆");
	public static final PcPositonError NO_MORE_MARGIN = new PcPositonError(20202, "没有可减少的保证金");

	public static final PcPositonError LIQING = new PcPositonError(20203, "强平中。。。");
	public static final PcPositonError FORCE_CLOSE = new PcPositonError(20204, "仓位已强平!");

	private PcPositonError(int code, String message) {
		super(code, message);
	}

}
