package com.hp.sh.expv3.pc.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;
/**
 * 风险基金：-13311 ~ -13319
 * @author wangjg
 *
 */
public class PcRiskfundAccountError extends ErrorCode {

	//user
	public static final PcRiskfundAccountError TYPE_ERROR = new PcRiskfundAccountError(-13311, "记录类型错误!");
	
	public static final PcRiskfundAccountError INCONSISTENT_REQUESTS = new PcRiskfundAccountError(-13312, "手续费账户变更请求不一致!");
	
	public static final PcRiskfundAccountError BALANCE_NOT_ENOUGH = new PcRiskfundAccountError(-13313, "余额不足!");
	
	private PcRiskfundAccountError(int code, String message) {
		super(code, message);
	}

}
