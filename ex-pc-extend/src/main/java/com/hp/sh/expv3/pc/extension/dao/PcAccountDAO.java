
package com.hp.sh.expv3.pc.extension.dao;

import com.hp.sh.expv3.pc.extension.vo.PcAccountVo;
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
	
	public PcAccountVo get(@Param("userId") Long userId, @Param("asset") String asset);

    public List<PcAccountVo> queryList(Map<String,Object> params);

    public PcAccountVo queryOne(Map<String,Object> params);


}
