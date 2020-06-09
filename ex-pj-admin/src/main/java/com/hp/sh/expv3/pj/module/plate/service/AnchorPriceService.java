
package com.hp.sh.expv3.pj.module.plate.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pj.module.plate.dao.AnchorPriceDAO;
import com.hp.sh.expv3.pj.module.plate.entity.AnchorPrice;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class AnchorPriceService{

	@Autowired
	private AnchorPriceDAO anchorPriceDAO;

	public AnchorPrice getAnchorPrice(Long id ) {
		return this.anchorPriceDAO.findById(id);
	}

	public void save(Long psId, BigDecimal price) {
		Long now = DbDateUtils.now();
		AnchorPrice ap = new AnchorPrice();
		ap.setPrice(price);
		ap.setCreated(now);
		ap.setPsId(psId);
		this.anchorPriceDAO.save(ap);
	}

	public void batchSave(List<AnchorPrice> list){
		Long now = DbDateUtils.now();
		for(AnchorPrice anchorPrice:list){
			if(anchorPrice.getId()==null){
				anchorPrice.setCreated(now);
				this.anchorPriceDAO.save(anchorPrice);
			}else{
				this.anchorPriceDAO.update(anchorPrice);
			}
		}
	}

}
