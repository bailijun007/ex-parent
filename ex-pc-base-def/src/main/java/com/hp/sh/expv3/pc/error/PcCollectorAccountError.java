package com.hp.sh.expv3.pc.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;
/**
 * 币币异常模块
 * -13001 ~ -13999 见： pc_exception
 * 账号：-13201 ~ -13299
 * @author wangjg
 *
 */
public class PcCollectorAccountError extends ErrorCode {

	//user
	public static final PcCollectorAccountError TYPE_ERROR = new PcCollectorAccountError(-13207, "记录类型错误!");
	
	public static final PcCollectorAccountError INCONSISTENT_REQUESTS = new PcCollectorAccountError(-13208, "手续费账户变更请求不一致!");
	
	public static final PcCollectorAccountError BALANCE_NOT_ENOUGH = new PcCollectorAccountError(-13209, "余额不足!");
	
	private PcCollectorAccountError(int code, String message) {
		super(code, message);
	}

}
