package com.hp.sh.expv3.bb.component;

import java.math.BigDecimal;
import java.util.List;

import com.hp.sh.expv3.bb.component.vo.BBContractVO;

/**
 * 面值查询
 * @author wangjg
 *
 */
public interface MetadataService {

	public BigDecimal getFaceValue(String asset, String symbol);

	BBContractVO getBBContract(String asset, String symbol);

	List<BBContractVO> getAllBBContract();
}
