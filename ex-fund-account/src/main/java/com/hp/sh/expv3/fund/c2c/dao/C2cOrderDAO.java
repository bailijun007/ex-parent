
package com.hp.sh.expv3.fund.c2c.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.extension.vo.C2cOrderVo;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * @author wangjg
 *
 */
public interface C2cOrderDAO extends BaseUserDataMapper<C2cOrder,Long>  {

	public List<C2cOrder> queryList(Map<String,Object> params);

    public C2cOrder queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

    void updateBySnAndUserId(C2cOrder c2cOrder);

    List<C2cOrderVo> pageQueryByPayStatus(@Param("payStatus") Integer payStatus, @Param("nextPage") Integer nextPage, @Param("pageSize")Integer pageSize, @Param("id")Long id);
}
