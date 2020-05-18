package com.hp.sh.expv3.pc.module.position.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.error.ExCommonError;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.constant.ChangeMarginOptType;
import com.hp.sh.expv3.pc.constant.LiqStatus;
import com.hp.sh.expv3.pc.constant.MarginMode;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.error.PcPositonError;
import com.hp.sh.expv3.pc.module.account.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderQueryService;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.module.symbol.service.PcAccountSymbolService;
import com.hp.sh.expv3.pc.strategy.PcStrategyContext;
import com.hp.sh.expv3.pc.vo.request.PcAddRequest;
import com.hp.sh.expv3.pc.vo.request.PcCutRequest;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.math.BigUtils;

/**
 * 仓位保证金
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcPositionMarginService {

	@Autowired
	private PcOrderQueryService orderQueryService;
	
	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private PcAccountSymbolService accountSymbolService;
	
	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	@Autowired
	private PcPositionDataService positionDataService;
    @Autowired
    private PcStrategyContext strategyContext;
	
    /**
     * 超过最大杠杆时，修改杠杆。只修改对象的值，不保存。
     * @param pos
     * @param leverage
     * @param modifiedTime
     */
	public void downLeverage(PcPosition pos, BigDecimal leverage, long modifiedTime){
		long userId = pos.getUserId();
		String asset = pos.getAsset();
		String symbol = pos.getSymbol();
		Integer longFlag = pos.getLongFlag();

        //修改设置
        PcAccountSymbol accountSymbol = this.accountSymbolService.getOrCreate(userId, asset, symbol);
        this.modifyAccountSymbol(accountSymbol, longFlag, leverage, modifiedTime);
        
		//重新计算预估强平价
		BigDecimal liqPrice = strategyContext.calcLiqPrice(pos);
		pos.setLiqPrice(liqPrice);
        
        //修改杠杆值
        pos.setLeverage(leverage);
	}
	
	@LockIt(key="U-${userId}")
	public boolean changeLeverage(long userId, String asset, String symbol, int marginMode, Integer longFlag, BigDecimal leverage){
        //逐渐or全仓
        if (marginMode != MarginMode.FIXED) {
            throw new ExSysException(ExCommonError.UNSUPPORTED);
        }

        Long now = DbDateUtils.now();
        
        PcAccountSymbol accountSymbol = this.accountSymbolService.getOrCreate(userId, asset, symbol);
        
        if(accountSymbol==null){
        	accountSymbol = this.accountSymbolService.create(userId, asset, symbol);
        }
        
        //当前仓位
        PcPosition pos = this.positionDataService.getCurrentPosition(userId, asset, symbol, longFlag);
        BigDecimal posVolume = BigDecimal.ZERO;
        if(pos!=null){
        	posVolume = pos.getVolume();
        }
        
		//检查参数
        BigDecimal _maxLeverage = feeRatioService.getMaxLeverage(userId, asset, symbol, posVolume);
        if(BigUtils.ltZero(leverage) || BigUtils.gt(leverage, _maxLeverage)){
        	throw new ExException(PcPositonError.PARAM_GT_MAX_LEVERAGE);
        }
        
        //是否有活动委托
        if (orderQueryService.hasActiveOrder(userId, asset, symbol, longFlag)) {
            throw new ExException(PcPositonError.HAVE_ACTIVE_ORDER);
        }
        
        //修改设置
        this.modifyAccountSymbol(accountSymbol, longFlag, leverage, now);
        
        if (pos != null && leverage.compareTo(pos.getLeverage()) != 0) {

            if (pos.getLiqStatus()==LiqStatus.FROZEN) {
                throw new ExException(PcPositonError.LIQING);
            }

            //减少杠杆
            if (leverage.compareTo(pos.getLeverage()) < 0) {
                BigDecimal initMargin = this.getNewInitMargin(pos, leverage);

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
	
	private BigDecimal getNewInitMargin(PcPosition pos, BigDecimal leverage){
        //新的仓位保证金
        BigDecimal initMargin = strategyContext.calcInitMargin(pos, leverage);
        return initMargin;
	}

	private void modifyAccountSymbol(PcAccountSymbol accountSymbol, Integer longFlag, BigDecimal leverage, long modifiedTime){
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
	@LockIt(key="U-${userId}")
	public void changeMargin(Long userId, String asset, String symbol, int longFlag, int optType, BigDecimal amount){
		//当前仓位
		PcPosition pos = this.positionDataService.getCurrentPosition(userId, asset, symbol, longFlag);
		if(pos==null){
			throw new ExSysException(ExCommonError.OBJ_DONT_EXIST);
		}
		
		//检查可减少的保证金
		if(optType==ChangeMarginOptType.CUT){
			BigDecimal diff = this.getMaxReducedMargin(pos);
			if(BigUtils.gt(amount, diff)){
				throw new ExException(PcPositonError.NO_MORE_MARGIN, amount, diff);
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
	@LockIt(key="U-${userId}")
	public void cutMargin(Long userId, String asset, String symbol, Long posId, BigDecimal amount){
		//当前仓位
		PcPosition pos = this.positionDataService.getPosition(userId, posId);
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
	
	private BigDecimal calcLiqPrice(PcPosition pos) {
		BigDecimal liqPrice = strategyContext.calcLiqPrice(pos);
		return liqPrice;
	}

	/**
	 * 强平保证金
	 * @return
	 */
	public BigDecimal getLiqMarginDiff(PcPosition pos){
		BigDecimal pnl = strategyContext.calcFloatingPnl(pos);
		BigDecimal posMargin = pos.getPosMargin();
		if(BigUtils.ltZero(pnl)){
			posMargin = posMargin.add(pnl);
		}
		
		//仓位所需保证金
		BigDecimal holdMargin = strategyContext.calHoldMargin(pos);
		if(BigUtils.gt(holdMargin, posMargin)){
			return BigDecimal.ZERO;
		}
		return posMargin.subtract(holdMargin);
	}

	/**
	 * 可减少的保证金
	 * @return
	 */
	public BigDecimal getMaxReducedMargin(PcPosition pos){
		BigDecimal pnl = strategyContext.calcFloatingPnl(pos);
		BigDecimal posMargin = pos.getPosMargin();
		if(BigUtils.ltZero(pnl)){//减掉浮亏
			posMargin = posMargin.add(pnl);
		}
		
		//所需保证金
		BigDecimal initMargin = strategyContext.calcInitMargin(pos);
		if(BigUtils.lt(posMargin, initMargin)){
			return BigDecimal.ZERO;
		}
		return posMargin.subtract(initMargin);
	}

	private void cutLeverageMargin(Long userId, String asset, Long posId, BigDecimal amount) {
		PcCutRequest request = new PcCutRequest();
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark("调低杠杆追加保证金");
		request.setTradeNo("ADD_TO_MARGIN-"+System.currentTimeMillis());
		request.setTradeType(PcAccountTradeType.LEVERAGE_ADD_MARGIN);
		request.setAssociatedId(posId);
		this.pcAccountCoreService.cut(request);
	}
	
	private void cutManualMargin(Long userId, String asset, Long posId, BigDecimal amount) {
		PcCutRequest request = new PcCutRequest();
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark("手动追加保证金");
		request.setTradeNo("ADD_TO_MARGIN-"+System.currentTimeMillis());
		request.setTradeType(PcAccountTradeType.ADD_TO_MARGIN);
		request.setAssociatedId(posId);
		this.pcAccountCoreService.cut(request);
	}
	
	private void returnManualMargin(Long userId, String asset, Long posId, BigDecimal amount) {
		PcAddRequest request = new PcAddRequest();
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark("仓位减少保证金");
		request.setTradeNo("REDUCE_MARGIN-"+System.currentTimeMillis());
		request.setTradeType(PcAccountTradeType.REDUCE_MARGIN);
		request.setAssociatedId(posId);
		this.pcAccountCoreService.add(request);
	}
	
	@LockIt(key="U-${userId}")
	public boolean setAutoAddFlag(long userId, String asset, String symbol, int longFlag, int autoAddFlag){
		//当前仓位
		PcPosition pos = this.positionDataService.getCurrentPosition(userId, asset, symbol, longFlag);
		if(pos==null){
			throw new ExSysException(ExCommonError.OBJ_DONT_EXIST);
		}
		pos.setAutoAddFlag(autoAddFlag);
		this.positionDataService.update(pos);
		return true;
	}
	
	public void autoAddMargin(PcPosition pos){
		BigDecimal requestPosInitMargin = strategyContext.calcInitMargin(pos);
        //超过了现有保证金,增加
        if (BigUtils.gt(requestPosInitMargin, pos.getPosMargin())) {
        	//扣余额
        	BigDecimal delta = requestPosInitMargin.subtract(pos.getPosMargin());
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
		PcCutRequest request = new PcCutRequest();
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark("自动追加保证金");
		request.setTradeNo("ADD_TO_MARGIN-"+System.currentTimeMillis());
		request.setTradeType(PcAccountTradeType.AUTO_ADD_MARGIN);
		request.setAssociatedId(posId);
		this.pcAccountCoreService.cut(request);
	}
	
	private BigDecimal getBalance(Long userId, String asset){
		return this.pcAccountCoreService.getBalance(userId, asset);
	}
}
