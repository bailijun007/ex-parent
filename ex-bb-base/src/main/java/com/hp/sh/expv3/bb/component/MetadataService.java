package com.hp.sh.expv3.bb.component;

import java.util.List;

import com.hp.sh.expv3.bb.component.vo.BBSymbolVO;

/**
 * 面值查询
 * @author wangjg
 *
 */
public interface MetadataService {

	BBSymbolVO getBBContract(String asset, String symbol);

	List<BBSymbolVO> getAllBBContract();
}
