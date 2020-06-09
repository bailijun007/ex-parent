package com.hp.sh.expv3.pj.module.plate.job;

import java.math.BigDecimal;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pj.module.plate.component.CacheService;
import com.hp.sh.expv3.pj.module.plate.entity.PlateStrategy;
import com.hp.sh.expv3.pj.module.plate.service.AnchorPriceService;
import com.hp.sh.expv3.pj.module.plate.service.PlateStrategyService;
import com.hp.sh.expv3.utils.IntBool;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class PlateStrategyJob extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(PlateStrategyJob.class);
    
    @Autowired
    private PlateStrategyService plateStrategyService;
    
    @Autowired
    private AnchorPriceService anchorPriceService;
	
	@Autowired
	private CacheService cacheService;
	
	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE)
	int start1234567890(){
		this.pause(1000*5);
//		this.start();
		return -1;
	}
	
	public void run(){
		while(true){
			try{
				
				PlateStrategy ps = this.getCachedPlateStrategy("USDT", "BYM_USDT");
				if(ps==null || ps.getEnabled()!=IntBool.YES){
					this.pause(1000*60);
					continue;
				}
				
				this.genPrice(ps);
				
				long millis = 1000L*RandomUtils.nextInt(ps.getDelayMin(), ps.getDelayMax());
				this.pause(millis);
				
			}catch(Exception e){
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private PlateStrategy getCachedPlateStrategy(String asset, String symbol) {
		PlateStrategy ps = this.cacheService.getCachedPlateStrategy(asset, symbol);
		if(ps==null){
			ps = this.plateStrategyService.findBySymbol(asset, symbol);
			this.cacheService.cachePlateStrategy(ps);
		}
		return ps;
	}

	private void genPrice(PlateStrategy ps) {
		Double lastPrice = this.cacheService.getCachedNewPrice(ps.getAsset(), ps.getSymbol());
		if(lastPrice==null){
			lastPrice = ps.getPriceMin();
		}
		double step = randomDouble(ps.getStepMin(),ps.getStepMax());
		double priceMin = ps.getPriceMin();
		double priceMax = ps.getPriceMax();

		double newPrice = lastPrice+step;
		
		if(newPrice < priceMin){
			newPrice = priceMin;
		}

		if(newPrice > priceMax){
			newPrice = priceMax;
		}
		
		BigDecimal bdNewPrice = new BigDecimal(newPrice);
		
		bdNewPrice = bdNewPrice.setScale(8, BigDecimal.ROUND_HALF_DOWN);
		
		this.anchorPriceService.save(ps.getId(), bdNewPrice);
		this.cacheService.cacheNewPrice(ps.getAsset(), ps.getSymbol(), bdNewPrice);
	}
	
	private void pause(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static double randomDouble(final double startInclusive, final double endInclusive){
		double diff = endInclusive-startInclusive;
		double rnd = RandomUtils.nextDouble(0, diff);
		double reuslt = startInclusive + rnd;
		return reuslt;
	}
	
	public static void main(String[] args) {
		double step = randomDouble(-1, 1);
		System.out.println(step);
	}

}
