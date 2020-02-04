package com.hp.sh.expv3.bb.module.position.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.component.FeeRatioService;
import com.hp.sh.expv3.bb.component.MarkPriceService;
import com.hp.sh.expv3.bb.constant.ChangeMarginOptType;
import com.hp.sh.expv3.bb.constant.LiqStatus;
import com.hp.sh.expv3.bb.constant.MarginMode;
import com.hp.sh.expv3.bb.constant.OrderFlag;
import com.hp.sh.expv3.bb.constant.BBAccountTradeType;
import com.hp.sh.expv3.bb.error.BBPositonError;
import com.hp.sh.expv3.bb.module.account.service.BBAccountCoreService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderQueryService;
import com.hp.sh.expv3.bb.module.position.entity.BBPosition;
import com.hp.sh.expv3.bb.module.symbol.entity.BBAccountSymbol;
import com.hp.sh.expv3.bb.module.symbol.service.BBAccountSymbolService;
import com.hp.sh.expv3.bb.strategy.HoldPosStrategy;
import com.hp.sh.expv3.bb.strategy.PositionStrategyContext;
import com.hp.sh.expv3.bb.strategy.common.CommonOrderStrategy;
import com.hp.sh.expv3.bb.vo.request.BBAddRequest;
import com.hp.sh.expv3.bb.vo.request.BBCutRequest;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.error.ExCommonError;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.math.BigUtils;

/**
 * 仓位保证金
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class BBPositionMarginService {

	@Autowired
	private BBOrderQueryService orderQueryService;
	
	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private BBAccountSymbolService accountSymbolService;
	
	@Autowired
	private BBAccountCoreService bBAccountCoreService;
	@Autowired
	private BBPositionDataService positionDataService;
	@Autowired
	private MarkPriceService markPriceService;
    @Autowired
    private PositionStrategyContext strategyContext;
    @Autowired
    private CommonOrderStrategy orderStrategy;
	
    /**
     * 超过最大杠杆时，修改杠杆。只修改对象的值，不保存。
     * @param pos
     * @param leverage
     * @param modifiedTime
     */
	public void downLeverage(BBPosition pos, BigDecimal leverage, long modifiedTime){
		long userId = pos.getUserId();
		String asset = pos.getAsset();
		String symbol = pos.getSymbol();
		Integer longFlag = pos.getLongFlag();

        //修改设置
        BBAccountSymbol accountSymbol = this.accountSymbolService.getOrCreate(userId, asset, symbol);
        this.modifyAccountSymbol(accountSymbol, longFlag, leverage, modifiedTime);
        
		//重新计算预估强平价
		BigDecimal liqPrice = strategyContext.calcLiqPrice(pos);
		pos.setLiqPrice(liqPrice);
        
        //修改杠杆值
        pos.setLeverage(leverage);
	}
	
	@LockIt(key="${userId}-${asset}-${symbol}")
	public boolean changeLeverage(long userId, String asset, String symbol, int marginMode, Integer longFlag, BigDecimal leverage){
        //逐渐or全仓
        if (marginMode != MarginMode.FIXED) {
            throw new ExSysException(ExCommonError.UNSUPPORTED);
        }

        Long now = DbDateUtils.now();
        
        BBAccountSymbol accountSymbol = this.accountSymbolService.getOrCreate(userId, asset, symbol);
        
        if(accountSymbol==null){
        	accountSymbol = this.accountSymbolService.create(userId, asset, symbol);
        }
        
        //当前仓位
        BBPosition pos = this.positionDataService.getCurrentPosition(userId, asset, symbol, longFlag);
        BigDecimal posVolume = BigDecimal.ZERO;
        if(pos!=null){
        	posVolume = pos.getVolume();
        }
        
		//检查参数
        BigDecimal _maxLeverage = feeRatioService.getMaxLeverage(userId, asset, symbol, posVolume);
        if(BigUtils.ltZero(leverage) || BigUtils.gt(leverage, _maxLeverage)){
        	throw new ExException(BBPositonError.PARAM_GT_MAX_LEVERAGE);
        }
        
        //是否有活动委托
        if (orderQueryService.hasActiveOrder(userId, asset, symbol, longFlag)) {
            throw new ExException(BBPositonError.HAVE_ACTIVE_ORDER);
        }
        
        //修改设置
        this.modifyAccountSymbol(accountSymbol, longFlag, leverage, now);
        
        if (pos != null && leverage.compareTo(pos.getLeverage()) != 0) {

            if (pos.getLiqStatus()==LiqStatus.FROZEN) {
                throw new ExException(BBPositonError.LIQING);
            }

            //减少杠杆
            if (leverage.compareTo(pos.getLeverage()) < 0) {
                BigDecimal initMargin = this.getInitMargin(pos, leverage);

                //超过了现有保证金,增加
                if (BigUtils.gt(initMargin, pos.getPosMargin())) {
                	//扣余额
                	BigDecimal delta = initMargin.subtract(pos.getPosMargin());
                	this.cutLeverageMargin(userId, asset, pos.getId(), delta);

                	//增加保证金
                	pos.setPosMargin(initMargin);
                }

            }

            /* 修改强平价 */
    		
    		//重新计算预估强平价
    		BigDecimal liqPrice = strategyContext.calcLiqPrice(pos);
    		pos.setLiqPrice(liqPrice);
            
            //修改杠杆值
            pos.setLeverage(leverage);
            //保存仓位
            pos.setModified(now);
            this.positionDataService.update(pos);
        }
        
		return true;
	}
	
	private BigDecimal getInitMargin(BBPosition pos, BigDecimal leverage){
        BigDecimal amount = pos.getVolume().multiply(pos.getFaceValue());
        BigDecimal markPrice = markPriceService.getCurrentMarkPrice(pos.getAsset(), pos.getSymbol());
        //新的仓位保证金
        BigDecimal initMarginRatio = feeRatioService.getInitedMarginRatio(leverage);
		HoldPosStrategy holdPosStrategy = this.strategyContext.getHoldPosStrategy(pos.getAsset(), pos.getSymbol());
        BigDecimal initMargin = holdPosStrategy.calcInitMargin(pos.getLongFlag(), initMarginRatio, amount, pos.getMeanPrice(), markPrice);
        return initMargin;
	}

	private void modifyAccountSymbol(BBAccountSymbol accountSymbol, Integer longFlag, BigDecimal leverage, long modifiedTime){
        if(longFlag==OrderFlag.TYPE_LONG){
        	if(BigUtils.eq(accountSymbol.getLongLeverage(), leverage)){
        		return;
        	}
        	accountSymbol.setLongLeverage(leverage);
        }else{
        	if(BigUtils.eq(accountSymbol.getShortLeverage(), leverage)){
        		return;
        	}
        	accountSymbol.setShortLeverage(leverage);
        }
        accountSymbol.setModified(modifiedTime);
        this.accountSymbolService.update(accountSymbol);
	}

	/**
	 * 
	 * @param userId
	 * @param asset
	 * @param symbol
	 * @param longFlag
	 * @param optType 0-增加保证金，1-减少保证金
	 * @param amount
	 */
	@LockIt(key="${userId}-${asset}-${symbol}")
	public void changeMargin(Long userId, String asset, String symbol, int longFlag, int optType, BigDecimal amount){
		//当前仓位
		BBPosition pos = this.positionDataService.getCurrentPosition(userId, asset, symbol, longFlag);
		if(pos==null){
			throw new ExSysException(ExCommonError.OBJ_DONT_EXIST);
		}
		
		//检查可减少的保证金
		if(optType==ChangeMarginOptType.CUT){
			BigDecimal diff = this.getMaxReducedMargin(pos);
			if(BigUtils.gt(amount, diff)){
				throw new ExException(BBPositonError.NO_MORE_MARGIN, amount, diff);
			}
		}
		
		//修改pc_account余额
		//增加仓位保证金
		if(optType==ChangeMarginOptType.ADD){
			this.cutManualMargin(userId, asset, pos.getId(), amount);
			pos.setPosMargin(pos.getPosMargin().add(amount));
		}else{
			this.returnManualMargin(userId, asset, pos.getId(), amount);
			pos.setPosMargin(pos.getPosMargin().subtract(amount));
		}
		
		//重新计算强平价
		BigDecimal liqPrice = this.calcLiqPrice(pos);
		pos.setLiqPrice(liqPrice);
		
		//保存
		pos.setModified(DbDateUtils.now());
		this.positionDataService.update(pos);
	}
	
	/**
	 * 不验证，直接减，用于手动强平
	 * @param userId
	 * @param asset
	 * @param symbol
	 * @param longFlag
	 * @param amount
	 */
	@Deprecated
	@LockIt(key="${userId}-${asset}-${symbol}")
	public void cutMargin(Long userId, String asset, String symbol, Long posId, BigDecimal amount){
		//当前仓位
		BBPosition pos = this.positionDataService.getPosition(userId, posId);
		if(pos==null){
			throw new ExSysException(ExCommonError.OBJ_DONT_EXIST);
		}
		//减少仓位保证金
		this.returnManualMargin(userId, pos.getAsset(), pos.getId(), amount);
		pos.setPosMargin(pos.getPosMargin().subtract(amount));
		
		//重新计算强平价
		BigDecimal liqPrice = this.calcLiqPrice(pos);
		pos.setLiqPrice(liqPrice);
		
		//保存
		pos.setModified(DbDateUtils.now());
		this.positionDataService.update(pos);
	}
	
	private BigDecimal calcLiqPrice(BBPosition pos) {
		BigDecimal liqPrice = strategyContext.calcLiqPrice(pos);
		return liqPrice;
	}

	/**
	 * 强平保证金
	 * @return
	 */
	public BigDecimal getLiqMarginDiff(BBPosition pos){
		BigDecimal markPrice = this.markPriceService.getCurrentMarkPrice(pos.getAsset(), pos.getSymbol());
		HoldPosStrategy holdPosStrategy = this.strategyContext.getHoldPosStrategy(pos.getAsset(), pos.getSymbol());
		BigDecimal pnl = holdPosStrategy.calcPnl(pos.getLongFlag(), pos.getVolume().multiply(pos.getFaceValue()), pos.getMeanPrice(), markPrice);
		BigDecimal posMargin = pos.getPosMargin();
		if(BigUtils.ltZero(pnl)){
			posMargin = posMargin.add(pnl);
		}
		
		//所需保证金
		BigDecimal holdMarginRatio = pos.getHoldMarginRatio();
		BigDecimal holdMargin = orderStrategy.calMargin(pos.getVolume(), pos.getFaceValue(), pos.getMeanPrice(), holdMarginRatio);
		if(BigUtils.gt(holdMargin, posMargin)){
			return BigDecimal.ZERO;
		}
		return posMargin.subtract(holdMargin);
	}

	/**
	 * 可减少的保证金
	 * @return
	 */
	public BigDecimal getMaxReducedMargin(BBPosition pos){
		BigDecimal markPrice = this.markPriceService.getCurrentMarkPrice(pos.getAsset(), pos.getSymbol());
		HoldPosStrategy holdPosStrategy = this.strategyContext.getHoldPosStrategy(pos.getAsset(), pos.getSymbol());
		BigDecimal pnl = holdPosStrategy.calcPnl(pos.getLongFlag(), pos.getVolume().multiply(pos.getFaceValue()), pos.getMeanPrice(), markPrice);
		BigDecimal posMargin = pos.getPosMargin();
		if(BigUtils.ltZero(pnl)){//减掉浮亏
			posMargin = posMargin.add(pnl);
		}
		
		//所需保证金
		BigDecimal initMarginRatio = feeRatioService.getInitedMarginRatio(pos.getLeverage());
		BigDecimal initMargin = orderStrategy.calMargin(pos.getVolume(), pos.getFaceValue(), pos.getMeanPrice(), initMarginRatio);
		if(BigUtils.lt(posMargin, initMargin)){
			return BigDecimal.ZERO;
		}
		return posMargin.subtract(initMargin);
	}

	private void cutLeverageMargin(Long userId, String asset, Long posId, BigDecimal amount) {
		BBCutRequest request = new BBCutRequest();
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark("调低杠杆追加保证金");
		request.setTradeNo("ADD_TO_MARGIN-"+System.currentTimeMillis());
		request.setTradeType(BBAccountTradeType.LEVERAGE_ADD_MARGIN);
		request.setAssociatedId(posId);
		this.bBAccountCoreService.cut(request);
	}
	
	private void cutManualMargin(Long userId, String asset, Long posId, BigDecimal amount) {
		BBCutRequest request = new BBCutRequest();
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark("手动追加保证金");
		request.setTradeNo("ADD_TO_MARGIN-"+System.currentTimeMillis());
		request.setTradeType(BBAccountTradeType.ADD_TO_MARGIN);
		request.setAssociatedId(posId);
		this.bBAccountCoreService.cut(request);
	}
	
	private void returnManualMargin(Long userId, String asset, Long posId, BigDecimal amount) {
		BBAddRequest request = new BBAddRequest();
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark("仓位减少保证金");
		request.setTradeNo("REDUCE_MARGIN-"+System.currentTimeMillis());
		request.setTradeType(BBAccountTradeType.REDUCE_MARGIN);
		request.setAssociatedId(posId);
		this.bBAccountCoreService.add(request);
	}
	
	@LockIt(key="${userId}-${asset}-${symbol}")
	public boolean setAutoAddFlag(long userId, String asset, String symbol, int longFlag, int autoAddFlag){
		//当前仓位
		BBPosition pos = this.positionDataService.getCurrentPosition(userId, asset, symbol, longFlag);
		if(pos==null){
			throw new ExSysException(ExCommonError.OBJ_DONT_EXIST);
		}
		pos.setAutoAddFlag(autoAddFlag);
		this.positionDataService.update(pos);
		return true;
	}
	
	public void autoAddMargin(BBPosition pos){
		BigDecimal requestPosMargin = strategyContext.calcInitMargin(pos);
        //超过了现有保证金,增加
        if (BigUtils.gt(requestPosMargin, pos.getPosMargin())) {
        	//扣余额
        	BigDecimal delta = requestPosMargin.subtract(pos.getPosMargin());
        	BigDecimal balance = this.getBalance(pos.getUserId(), pos.getAsset());
        	delta = delta.min(balance);
    		this.cutAutoMargin(pos.getUserId(), pos.getAsset(), pos.getId(), delta);
    		pos.setPosMargin(pos.getPosMargin().add(delta));
    		
    		//重新计算强平价
    		BigDecimal liqPrice = this.calcLiqPrice(pos);
    		pos.setLiqPrice(liqPrice);
    		
    		pos.setModified(DbDateUtils.now());
    		this.positionDataService.update(pos);
        }
	}
	
	private void cutAutoMargin(Long userId, String asset, Long posId, BigDecimal amount) {
		BBCutRequest request = new BBCutRequest();
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark("自动追加保证金");
		request.setTradeNo("ADD_TO_MARGIN-"+System.currentTimeMillis());
		request.setTradeType(BBAccountTradeType.AUTO_ADD_MARGIN);
		request.setAssociatedId(posId);
		this.bBAccountCoreService.cut(request);
	}
	
	private BigDecimal getBalance(Long userId, String asset){
		return this.bBAccountCoreService.getBalance(userId, asset);
	}
}
