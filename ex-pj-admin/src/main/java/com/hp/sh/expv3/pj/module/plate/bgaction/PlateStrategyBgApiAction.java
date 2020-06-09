
package com.hp.sh.expv3.pj.module.plate.bgaction;
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
import com.hp.sh.expv3.pj.module.plate.entity.PlateStrategy;
import com.hp.sh.expv3.pj.module.plate.service.PlateStrategyService;
import com.hp.sh.expv3.utils.CheckUtils;

/**
 * 锚定价格接口
 * @author wangjg
 */
@RestController
@RequestMapping("/admin/api/plate")
public class PlateStrategyBgApiAction extends BaseBizAction{
	@Autowired
	private PlateStrategyService plateStrategyService;
	
	@Autowired
	private CacheService cacheService;

	/**
	 * 锚定价格列表
	 * @param keyword 查询关键字
	 */
	@RequestMapping("/list")
	public PageResult<PlateStrategy> list(Integer pageNo){
		Page page = this.getPage(pageNo);
		List<PlateStrategy> list = plateStrategyService.pageQuery(page);
		return this.getPageResult(page, list);
	}

	/**
	 * 获取锚定价格
	 * @param id 编号
	 * @return
	 */
	@RequestMapping("/get")
	public PlateStrategy get(Long id){
		PlateStrategy plateStrategy = plateStrategyService.getPlateStrategy(id);
		return plateStrategy;
	}

	/**
	 * 保存锚定价格
	 */
	@RequestMapping("/save")
	public void save(@RequestBody PlateStrategy ps){
		
		CheckUtils.checkRange(ps.getDelayMin(), ps.getDelayMax());
		CheckUtils.checkRange(ps.getPriceMin(), ps.getPriceMax());
		CheckUtils.checkRange(ps.getStepMin(), ps.getStepMax());
		CheckUtils.checkRange(ps.getVolumeMin(), ps.getVolumeMax());
		
		PlateStrategy ps1 = this.plateStrategyService.getPlateStrategy(ps.getId());
		
		if(ps1==null){
			this.plateStrategyService.save(ps);
		}else{
			BeanHelper.copyNonNull(ps, ps1);
			this.plateStrategyService.update(ps1);
		}
		
		cacheService.cachePlateStrategy(ps1);
		
	}

}
