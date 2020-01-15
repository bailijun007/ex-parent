package com.hp.sh.expv3.pc.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.gitee.hupadev.commons.bean.BeanHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.extension.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.extension.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcPositionNumStatVo;
import com.hp.sh.expv3.pc.extension.vo.PcPositionVo;
import com.hp.sh.expv3.pc.extension.vo.PcPositionVolumeStatVo;
import com.hp.sh.expv3.pc.extension.vo.PcSymbolPositionStatVo;
import com.hp.sh.expv3.pc.extension.vo.PcSymbolPositionTotalVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcPositionExtendServiceImpl implements PcPositionExtendService {
    @Autowired
    private PcPositionDAO pcPositionDAO;

    @Autowired
    private PcOrderTradeDAO pcOrderTradeDAO;

    @Override
    public BigDecimal getPosMargin(Long userId, String asset) {
        if (userId == null) {
            return BigDecimal.ZERO;
        }
        return pcPositionDAO.getPosMargin(userId, asset);
    }

    @Override
    public BigDecimal getPl(Long userId, String asset, Long posId) {
        BigDecimal bigDecimal = pcOrderTradeDAO.getPl(userId, asset, posId);
        return bigDecimal;
    }

    @Override
    public BigDecimal getPlRatio(Long userId, String asset, Long posId) {
        BigDecimal pl = this.getPl(userId, asset, posId);
        BigDecimal initMargin = pcPositionDAO.getInitMargin(userId, asset, posId);
        if(initMargin.compareTo(new BigDecimal(0))==0){
            //初始保证金不能为0
            throw new ExException(PcCommonErrorCode.INIT_MARGIN_NOT_EQUAL_ZERO);
        }
        return pl.divide(initMargin);
    }

    @Override
    public PageResult<PcPositionVo> pageQueryPositionList(Long userId, String asset, String symbol, Long posId, Integer liqStatus, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        PageResult<PcPositionVo> pageResult=new PageResult<>();
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("id",posId);
        map.put("liqStatus",liqStatus);
        List<PcPositionVo> pcPositionVos = pcPositionDAO.queryList(map);
        PageInfo<PcPositionVo> info = new PageInfo<>(pcPositionVos);
        pageResult.setList(pcPositionVos);
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        pageResult.setRowTotal(info.getTotal());
        return pageResult;
    }

    @Override
    public List<PcPositionVo> findActivePosition(Long userId, String asset, String symbol) {
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        map.put("symbol",symbol);
        List<PcPositionVo> pcPositionVos = pcPositionDAO.queryActivePosition(map);
        return pcPositionVos;
    }

    /**
     * 均价，仓位为0时，表示最后一次仓位变动时的均价meanPrice
     * @param userId
     * @param asset
     * @param symbol
     * @return
     */
    @Override
    public BigDecimal getAvgPrice(Long userId, String asset, String symbol) {
        Integer volume=0;
        return pcPositionDAO.getAvgPrice(userId,asset,symbol,volume);

    }

    @Override
    public List<PcPositionVo> selectPosByAccount(Long userId, String asset, String symbol, Long startTime) {
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("modifiedBegin",startTime);
        List<PcPositionVo> pcPositionVos = pcPositionDAO.selectPosByAccount(map);
        return pcPositionVos;
    }

	@Override
	public List<PcSymbolPositionStatVo> getSymbolPositionStat(String asset, String symbol) {
		List<PcSymbolPositionStatVo> list = this.pcPositionDAO.getSymbolPositionStat(asset, symbol);
		return list;
	}

	@Override
	public PcSymbolPositionTotalVo getSymbolPositionTotal(String asset, String symbol) {
		PcSymbolPositionTotalVo result = new PcSymbolPositionTotalVo();
		List<PcPositionVolumeStatVo> volStat = this.pcPositionDAO.getPositionVolumeStat(asset, symbol);
		Map<Object, PcPositionVolumeStatVo> volMap = BeanHelper.listToMap(volStat, "longFlag");
		
		
		List<PcPositionNumStatVo> numStat = this.pcPositionDAO.getPositionNumStat(asset, symbol);
		Map<Object, PcPositionNumStatVo> numMap = BeanHelper.listToMap(numStat, "longFlag");
		

		BigDecimal longVolume = BigDecimal.ZERO;
		BigDecimal shortVolume = BigDecimal.ZERO;
		BigDecimal longPosNum = BigDecimal.ZERO;
		BigDecimal shortPosNum = BigDecimal.ZERO;
		
		if(volMap.get(OrderFlag.TYPE_LONG)!=null){
			longVolume = volMap.get(OrderFlag.TYPE_LONG).getVolume();
		}
		if(volMap.get(OrderFlag.TYPE_SHORT)!=null){
			shortVolume = volMap.get(OrderFlag.TYPE_SHORT).getVolume();
		}
		if(numMap.get(OrderFlag.TYPE_LONG)!=null){
			longPosNum = numMap.get(OrderFlag.TYPE_LONG).getPosNum();
		}
		if(numMap.get(OrderFlag.TYPE_SHORT)!=null){
			shortPosNum = numMap.get(OrderFlag.TYPE_SHORT).getPosNum();
		}
		
		result.setAsset(asset);
		result.setSymbol(symbol);
		result.setLongVolume(longVolume);
		result.setShortVolume(shortVolume);
		result.setLongPosNum(longPosNum);
		result.setShortPosNum(shortPosNum);
		return result;
	}
}
