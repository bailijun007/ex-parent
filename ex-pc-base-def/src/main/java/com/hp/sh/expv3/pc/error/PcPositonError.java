package com.hp.sh.expv3.pc.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * pc永续合约异常模块
 * -13001 ~ -13999 见： pc_exception
 * 仓位：-13101 ~ -13199
 * @author wangjg
 *
 */
public class PcPositonError extends ErrorCode {

	public static final PcPositonError POS_NOT_ENOUGH = new PcPositonError(-13101, "超过了可平仓位!");
	public static final PcPositonError HAVE_ACTIVE_ORDER = new PcPositonError(-13102, "当前存在挂单，不能修改杠杆");
	public static final PcPositonError NO_MORE_MARGIN = new PcPositonError(-13103, "超过了最多仓位可减少保证金");
	
	public static final PcPositonError PARAM_GT_MAX_LEVERAGE = new PcPositonError(-13104, "超过最大杠杆!");

	public static final PcPositonError LIQING = new PcPositonError(-13105, "强平中。。。");
	public static final PcPositonError FORCE_CLOSE = new PcPositonError(-13106, "仓位已强平!");

	private PcPositonError(int code, String message) {
		super(code, message);
	}

}
