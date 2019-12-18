package com.hp.sh.expv3.pc.module.position.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.pc.calc.CompFieldCalc;
import com.hp.sh.expv3.pc.component.FeeCollectorSelector;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.component.MarkPriceService;
import com.hp.sh.expv3.pc.constant.LiqStatus;
import com.hp.sh.expv3.pc.constant.MarginMode;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.pc.error.PositonError;
import com.hp.sh.expv3.pc.module.account.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.symbol.dao.PcAccountSymbolDAO;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.strategy.aabb.AABBMetadataService;
import com.hp.sh.expv3.pc.strategy.aabb.AABBPositionStrategy;
import com.hp.sh.expv3.pc.strategy.aabb.PnlCalc;
import com.hp.sh.expv3.pc.vo.request.PcAddRequest;
import com.hp.sh.expv3.pc.vo.request.PcCutRequest;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;
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
	private PcPositionDAO pcPositionDAO;
	
	@Autowired
	private PcOrderTradeDAO pcOrderTradeDAO;
	
	@Autowired
	private PcOrderDAO pcOrderDAO;
	
	@Autowired
	private PcAccountSymbolDAO pcAccountSymbolDAO;

	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	
	@Autowired
	private FeeCollectorSelector feeCollectorSelector;
	
	@Autowired
	private AABBPositionStrategy positionStrategy;
	
	@Autowired
	private MarkPriceService markPriceService;
	@Autowired
	private AABBMetadataService metadataService;
	
	@LockIt(key="${userId}-${asset}-${symbol}")
	public boolean changeLeverage(long userId, String asset, String symbol, int marginMode, Integer longFlag, BigDecimal leverage){
        //逐渐or全仓
        if (marginMode != MarginMode.FIXED) {
            throw new ExException(CommonError.PARAM_ERROR);
        }
        
        PcAccountSymbol accountSymbol = this.getAccountSymbol(userId, asset, symbol);
        
        //检查参数
        if(BigUtils.ltZero(leverage) || BigUtils.gt(leverage, accountSymbol.getLongMaxLeverage())){
        	throw new ExException(CommonError.PARAM_ERROR);
        }
        
        //是否有活跃委托
        if (this.hasActiveOrder(userId, asset, symbol, longFlag)) {
            throw new ExException(PositonError.HAVE_ACTIVE_ORDER);
        }
        
        //修改设置
        this.modifyAccountSymbol(accountSymbol, longFlag, leverage);
        
        //当前仓位
        PcPosition pos = this.pcPositionDAO.getActivePos(userId, asset, symbol, longFlag);
        
        if (pos != null && leverage.compareTo(pos.getLeverage()) != 0) {

            if (pos.getLiqStatus()==LiqStatus.YES) {
                throw new ExException(PositonError.LIQING);
            }

            Date now = DbDateUtils.now();

            //减少杠杆
            if (leverage.compareTo(pos.getLeverage()) < 0) {
                BigDecimal feeRatio = feeRatioService.getCloseFeeRatio(userId, pos.getAsset(), pos.getSymbol());

                /* 修改保证金 */
                //标记价格
                BigDecimal amount = pos.getVolume().multiply(metadataService.getFaceValue(asset, symbol));
                BigDecimal markPrice = markPriceService.getCurrentMarkPrice(asset, symbol);
                //新的仓位保证金
                BigDecimal requestPosMargin = this.calcMarginWhenChangeLeverage(longFlag, leverage, amount, feeRatio, pos.getMeanPrice(), markPrice);

                //超过了现有保证金,增加
                if (requestPosMargin.compareTo(pos.getPosMargin()) > 0) {
                	//扣余额
                	BigDecimal delta = requestPosMargin.subtract(pos.getPosMargin());
                	this.cutPcAccount(userId, asset, pos.getId(), delta);

                	//增加保证金
                	pos.setPosMargin(requestPosMargin);
                }

            }

            /* 修改强平价 */
    		
    		//重新计算强平价
    		BigDecimal liqPrice = this.calcLiqPrice(pos.getAsset(), pos.getSymbol(), longFlag, pos.getVolume(), pos.getMeanPrice(), pos.getHoldRatio(), pos.getPosMargin());
    		pos.setLiqPrice(liqPrice);
            
            //修改杠杆值
            pos.setLeverage(leverage);
            //保存仓位
            pos.setModified(now);
            this.pcPositionDAO.update(pos);
        }
        
		return true;
	}
	
	/*
	 * 用 均价 标记价格 为实现盈亏计算 保证金
	 * @param longFlag
	 * @param leverage
	 * @param amount
	 * @param feeRatio
	 * @param meanPrice
	 * @param markPrice
	 * @return
	 */
	//TODO AABB
	private BigDecimal calcMarginWhenChangeLeverage(Integer longFlag, BigDecimal leverage, BigDecimal amount, BigDecimal feeRatio, BigDecimal meanPrice, BigDecimal markPrice) {
        // ( 1 / leverage ) * volume = volume / leverage
        BigDecimal pnl = PnlCalc.calcPnl(longFlag, amount, meanPrice, markPrice);
        BigDecimal initMarginRatio = feeRatioService.getInitedMarginRatio(leverage);
        BigDecimal baseValue = CompFieldCalc.calcBaseValue(amount, meanPrice);
        return baseValue.multiply(initMarginRatio).subtract(pnl.min(BigDecimal.ZERO)).stripTrailingZeros();
	}

	private void modifyAccountSymbol(PcAccountSymbol accountSymbol, Integer longFlag, BigDecimal leverage){
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
        Date now = DbDateUtils.now();
		accountSymbol.setModified(now);
        this.pcAccountSymbolDAO.update(accountSymbol);
	}
	
	private PcAccountSymbol getAccountSymbol(long userId, String asset, String symbol) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("asset", asset);
		params.put("symbol", symbol);
		PcAccountSymbol pcAccountSymbol = this.pcAccountSymbolDAO.queryOne(params );
		return pcAccountSymbol;
	}

	private boolean hasActiveOrder(long userId, String asset, String symbol, Integer longFlag) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("asset", asset);
		params.put("symbol", symbol);
		params.put("longFlag", longFlag);
		params.put("activeFlag", IntBool.YES);
		Long count = this.pcOrderDAO.queryCount(params);
		return count>0;
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
		PcPosition pos = this.pcPositionDAO.getActivePos(userId, asset, symbol, longFlag);
		if(pos==null){
			throw new ExException(CommonError.OBJ_DONT_EXIST);
		}
		
		//检查可减少的保证金
		if(optType==1){
			BigDecimal diff = this.getMinMarginDiff(pos);
			if(BigUtils.ltZero(diff)){
				throw new ExException(PositonError.NO_MORE_MARGIN);
			}
		}
		
		//修改pc_account余额
		if(optType==0){
			this.cutPcAccount(userId, asset, pos.getId(), amount);
		}else{
			this.addPcAccount(userId, asset, pos.getId(), amount);
		}
		
		//增加仓位保证金
		if(optType==0){
			pos.setPosMargin(pos.getPosMargin().add(amount));
		}else{
			pos.setPosMargin(pos.getPosMargin().subtract(amount));
		}
		
		//重新计算强平价
		BigDecimal liqPrice = this.calcLiqPrice(pos.getAsset(), pos.getSymbol(), longFlag, pos.getVolume(), pos.getMeanPrice(), pos.getHoldRatio(), pos.getPosMargin());
		pos.setLiqPrice(liqPrice);
		
		//保存
		this.pcPositionDAO.update(pos);
	}
	
	/**
	 * 可减少的保证金
	 * @return
	 */
	public BigDecimal getMinMarginDiff(PcPosition pos){
		BigDecimal markPrice = this.markPriceService.getCurrentMarkPrice(pos.getAsset(), pos.getSymbol());
		BigDecimal pnl = PnlCalc.calcPnl(pos.getLongFlag(), pos.getVolume().multiply(this.metadataService.getFaceValue(pos.getAsset(), pos.getSymbol())), pos.getMeanPrice(), markPrice);
		BigDecimal posMargin = pos.getInitMargin();
		if(BigUtils.ltZero(pnl)){
			posMargin = posMargin.subtract(pnl);
		}
		//所需保证金
		BigDecimal margin = BigDecimal.ONE.divide(pos.getLeverage());
		if(BigUtils.gt(margin, posMargin)){
			return BigDecimal.ZERO;
		}
		return posMargin.subtract(posMargin);
	}
	
	private BigDecimal calcLiqPrice(String asset, String symbol, int longFlag, BigDecimal volume, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		BigDecimal liqPrice = this.positionStrategy.calcLiqPrice(asset, symbol, longFlag, volume, openPrice, holdMarginRatio, posMargin);
		return liqPrice;
	}
	
	private void cutPcAccount(Long userId, String asset, Long posId, BigDecimal amount) {
		PcCutRequest request = new PcCutRequest();
		request.setAmount(request.getAmount());
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark("仓位追加保证金");
		request.setTradeNo("ADD_TO_MARGIN-"+System.currentTimeMillis());
		request.setTradeType(PcAccountTradeType.ADD_TO_MARGIN);
		request.setAssociatedId(posId);
		this.pcAccountCoreService.cut(request);
	}
	
	private void addPcAccount(Long userId, String asset, Long posId, BigDecimal amount) {
		PcAddRequest request = new PcAddRequest();
		request.setAmount(request.getAmount());
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark("仓位减少保证金");
		request.setTradeNo("REDUCE_MARGIN-"+System.currentTimeMillis());
		request.setTradeType(PcAccountTradeType.REDUCE_MARGIN);
		request.setAssociatedId(posId);
		this.pcAccountCoreService.add(request);
	}
	
}
