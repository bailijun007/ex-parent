package com.hp.sh.expv3.pc.component;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.strategy.vo.PcContractVO;

/**
 * 面值查询
 * @author wangjg
 *
 */
public interface MetadataService {

	public BigDecimal getFaceValue(String asset, String symbol);

	PcContractVO getPcContract(String asset, String symbol);
}
