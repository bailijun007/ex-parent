/**
 * 
 */
package com.hp.sh.expv3.bb.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageExtService;
import com.hp.sh.expv3.bb.vo.result.StatItem;

/**
 * 币币账户核心接口
 * @author wangjg
 */
@RestController
public class BBMessageApiAction implements BBMessageApi {

	@Autowired
	private BBMessageExtService messageExtService;

	public List<StatItem> getSymbolStat(){
		List<StatItem> list = this.messageExtService.getSymbolStat();
		return list;
	}

	public BigDecimal getTotal(){
		List<StatItem> list = this.messageExtService.getSymbolStat();
		BigDecimal total = BeanHelper.sum(list, "num");
		return total;
	}
	
}