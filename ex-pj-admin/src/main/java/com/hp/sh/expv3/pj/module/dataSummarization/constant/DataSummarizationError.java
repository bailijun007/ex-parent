package com.hp.sh.expv3.pj.module.dataSummarization.constant;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 错误码 6101 -6199
 * 
 * @author wangjg
 *
 */
public class DataSummarizationError extends ErrorCode {

	public static final DataSummarizationError PARAM_NULL_ERROR = new DataSummarizationError(6101, "缺少必填参数!");
    public static final DataSummarizationError CALLING_THIRD_INTERFACE_ERROR  = new DataSummarizationError(6102, "调用第三方接口报错!");

	
	private DataSummarizationError(int code, String message) {
		super(code, message);
	}

}
