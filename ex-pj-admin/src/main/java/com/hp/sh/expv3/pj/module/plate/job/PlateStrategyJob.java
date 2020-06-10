package com.hp.sh.expv3.pj.module.plate.job;

import java.math.BigDecimal;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pj.module.plate.component.CacheService;
import com.hp.sh.expv3.pj.module.plate.entity.AnchorSetting;
import com.hp.sh.expv3.pj.module.plate.entity.PlateStrategy;
import com.hp.sh.expv3.pj.module.plate.service.AnchorPriceService;
import com.hp.sh.expv3.pj.module.plate.service.AnchorSettingService;
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
    private AnchorSettingService anchorSettingService;
	
	@Autowired
	private CacheService cacheService;
	
	@Value("${plate.strategy.start:true}")
	private boolean startPlateStrategy;
	
	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE)
	int start1234567890(){
		this.pause(1000*5);
		if(startPlateStrategy){
			this.start();
		}
		return -1;
	}
	
	public void run(){
		while(true){
			try{
				
				PlateStrategy ps = this.getCachedPlateStrategy("USDT", "BYM_USDT");
				if(ps==null){
					this.pause(1000*60);
					continue;
				}
				BigDecimal newPrice = null;
				
				if(ps.getEnabled()==IntBool.YES){
					newPrice = this.genPlatePrice(ps);
				}else{
					newPrice = this.genAnchorPrice(ps);
				}
				
				newPrice = newPrice.setScale(8, BigDecimal.ROUND_HALF_DOWN);
				
				this.anchorPriceService.save(ps.getId(), newPrice);
				this.cacheService.cacheNewestPrice(ps.getAsset(), ps.getSymbol(), newPrice);
				
				long millis = 1000L*RandomUtils.nextInt(ps.getDelayMin(), ps.getDelayMax());
				this.pause(millis);
				
			}catch(Exception e){
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private BigDecimal genAnchorPrice(PlateStrategy ps) {
		AnchorSetting as = this.anchorSettingService.findBySymbol(ps.getAsset(), ps.getSymbol());
		Double anchorPrice = this.cacheService.getCachedNewestPrice(as.getAnchorAsset(), as.getAnchorSymbol());
		
		double newPrice = anchorPrice*as.getAnchorRatio();
		
		double step = randomDouble(as.getSwingMin(),as.getSwingMax());
		
		if(step>0){
			if(newPrice > as.getExpectedPrice().doubleValue()){
				newPrice -= step;
			}else{
				newPrice += step;
			}
		}else{
			step = step * -1;
			if(newPrice > as.getExpectedPrice().doubleValue()){
				newPrice += step;
			}else{
				newPrice -= step;
			}
		}
		
		return new BigDecimal(newPrice);
	}

	private BigDecimal genPlatePrice(PlateStrategy ps) {
		Double lastPrice = this.cacheService.getCachedNewestPrice(ps.getAsset(), ps.getSymbol());
		if(lastPrice==null){
			lastPrice = ps.getPriceMin();
		}
		double step = randomDouble(ps.getStepMin(),ps.getStepMax());

		double newPrice = lastPrice+step;
		
		double priceMin = ps.getPriceMin();
		double priceMax = ps.getPriceMax();
		
		if(newPrice < priceMin){
			newPrice = priceMin;
		}

		if(newPrice > priceMax){
			newPrice = priceMax;
		}
		
		BigDecimal bdNewPrice = new BigDecimal(newPrice);
		
		return bdNewPrice;
	}
	
	private PlateStrategy getCachedPlateStrategy(String asset, String symbol) {
		PlateStrategy ps = this.cacheService.getCachedPlateStrategy(asset, symbol);
		if(ps==null){
			ps = this.plateStrategyService.findBySymbol(asset, symbol);
			this.cacheService.cachePlateStrategy(ps);
		}
		return ps;
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
