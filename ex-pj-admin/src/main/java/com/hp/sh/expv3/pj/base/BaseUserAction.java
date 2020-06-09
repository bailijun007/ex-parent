/**
 * 
 */
package com.hp.sh.expv3.pj.base;

import com.gitee.hupadev.base.exceptions.BizException;
import com.gitee.hupadev.base.exceptions.CommonError;

/**
 * @author wangjg
 */
public class BaseUserAction extends BaseBizAction {

	protected void setOwner(BaseUserDataEntity userData) {
		userData.setUserId(this.currentUserId());
	}

	protected void checkOwner(BaseUserDataEntity userData) {
		if(userData==null){
			throw new BizException(CommonError.OBJ_DONT_EXIST);
		}
		if(!this.currentUserId().equals(userData.getUserId())){
			throw new BizException(CommonError.ILLEGAL_OPERATION);
		}
	}

	
}
