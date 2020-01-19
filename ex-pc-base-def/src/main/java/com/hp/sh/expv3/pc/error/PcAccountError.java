package com.hp.sh.expv3.pc.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;
/**
 * pc永续合约异常模块
 * -13001 ~ -13999 见： pc_exception
 * 账号：-13201 ~ -13299
 * @author wangjg
 *
 */
public class PcAccountError extends ErrorCode {

	//user
	public static final PcAccountError TYPE_ERROR = new PcAccountError(-13201, "记录类型错误!");
	
	public static final PcAccountError INCONSISTENT_REQUESTS = new PcAccountError(-13202, "永续合约账户变更请求不一致!");
	
	public static final PcAccountError BALANCE_NOT_ENOUGH = new PcAccountError(-13203, "余额不足!");
	
	private PcAccountError(int code, String message) {
		super(code, message);
	}

}
