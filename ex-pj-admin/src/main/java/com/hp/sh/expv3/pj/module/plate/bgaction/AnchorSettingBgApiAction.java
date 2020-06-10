
package com.hp.sh.expv3.pj.module.plate.bgaction;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.commons.bean.PageResult;
import com.hp.sh.expv3.pj.base.BaseBizAction;
import com.hp.sh.expv3.pj.module.plate.component.CacheService;
import com.hp.sh.expv3.pj.module.plate.entity.AnchorSetting;
import com.hp.sh.expv3.pj.module.plate.service.AnchorSettingService;
import com.hp.sh.expv3.utils.CheckUtils;

/**
 * 锚定价格设置接口
 * @author wangjg
 */
@RestController
@RequestMapping("/admin/api/anchorSetting")
public class AnchorSettingBgApiAction extends BaseBizAction{
	@Autowired
	private AnchorSettingService anchorSettingService;	
	@Autowired
	private CacheService cacheService;

	/**
	 * 锚定价格设置列表
	 * @param keyword 查询关键字
	 */
	@RequestMapping("/list")
	public PageResult list(Integer pageNo){
		Page page = this.getPage(pageNo);
		List<AnchorSetting> list = anchorSettingService.pageQuery(page);
		return this.getPageResult(page, list);
	}

	/**
	 * 获取锚定价格设置
	 * @param id 编号
	 * @return
	 */
	@RequestMapping("/get")
	public AnchorSetting get(Long id){
		AnchorSetting as = anchorSettingService.getAnchorSetting(id);
		this.checkOwner(as);
		
		Double newestPrice = cacheService.getCachedNewestPrice(as.getAsset(), as.getSymbol());
		if(newestPrice!=null){
			as.setNewestPrice(new BigDecimal(newestPrice));
		}
		
		Double newestAnchorPrice = cacheService.getCachedNewestPrice(as.getAnchorAsset(), as.getAnchorSymbol());
		if(newestAnchorPrice!=null){
			as.setNewestAnchorPrice(new BigDecimal(newestAnchorPrice));
		}
		
		return as;
	}

	/**
	 * 保存锚定价格设置
	 */
	@RequestMapping("/save")
	public void save(@RequestBody AnchorSetting as){
		
		CheckUtils.checkRange(as.getSwingMin(), as.getSwingMax());
		CheckUtils.checkRequired("expectedPrice", "swingMin", "swingMax");
		
		double newestPrice = cacheService.getCachedNewestPrice(as.getAsset(), as.getSymbol());
		double newestAnchorPrice = cacheService.getCachedNewestPrice(as.getAnchorAsset(), as.getAnchorSymbol());
		double anchorRatio = as.getExpectedPrice().doubleValue()/newestAnchorPrice;
		as.setAnchorPrice(new BigDecimal(newestAnchorPrice));
		as.setAnchorRatio(anchorRatio);
		
		AnchorSetting as1 = this.anchorSettingService.getAnchorSetting(as.getId());
		
		if(as1==null){
			this.anchorSettingService.save(as);
		}else{
			BeanHelper.copyNonNull(as, as1);
			this.anchorSettingService.update(as1);
		}
		
	}

}
