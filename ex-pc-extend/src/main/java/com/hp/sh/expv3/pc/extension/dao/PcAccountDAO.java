
package com.hp.sh.expv3.pc.extension.dao;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.pc.module.account.entity.PcAccount;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author wangjg
 *
 */
public interface PcAccountDAO {
	
	public PcAccount get(@Param("userId") Long userId, @Param("asset") String asset);

    public List<PcAccount> queryList(Map<String,Object> params);


}
