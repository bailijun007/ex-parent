
package com.hp.sh.expv3.pc.module.order.service;

import java.util.List;

import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;

/**
 * 
 * @author wangjg
 *
 */
public interface PcOrderTradeService {

	public PcOrderTrade getPcOrderTrade(Long id);

	public List<PcOrderTrade> findByIds(List<Long> ids);

	public void save(PcOrderTrade pcOrderTrade);

	public void batchSave(List<PcOrderTrade> list);

	public void update(PcOrderTrade pcOrderTrade);

	public void delete(PcOrderTrade pcOrderTrade);

	public void delete(Long id);

	public void batchDelete(String idseq) ;

}
