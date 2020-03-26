package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.sh.expv3.pc.vo.response.ChangeMarginVo;
import com.hp.sh.expv3.pc.vo.response.CurPositionVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 仓位接口
 * @author wangjg
 *
 */
@Api(tags="仓位接口")
@FeignClient(value="ex-pc-base")
public interface PcPostionApi {

	@ApiOperation(value = "显示修改保证金信息")
	@GetMapping(value = "/api/pc/position/margin/showChangeMargin")
	ChangeMarginVo showChangeMargin(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol, @RequestParam("longFlag") Integer longFlag);
	
	@ApiOperation(value = "修改保证金")
	@GetMapping(value = "/api/pc/position/margin/changeMargin")
	void changeMargin(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol, @RequestParam("longFlag") Integer longFlag, @RequestParam("optType") Integer optType, @RequestParam("amount") BigDecimal amount);
	
	@ApiOperation(value = "修改杠杆")
	@GetMapping(value = "/api/pc/position/leverage/change")
	public boolean changeLeverage(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol, @RequestParam("marginMode") Integer marginMode, @RequestParam("longFlag") Integer longFlag, @RequestParam("leverage") BigDecimal leverage);

	@ApiOperation(value = "开关自动追加保证金")
	@GetMapping(value = "/api/pc/position/setAutoAddFlag")
	boolean setAutoAddFlag(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol, @RequestParam("longFlag") Integer longFlag, @RequestParam("autoAddFlag") Integer autoAddFlag);

	@ApiOperation(value = "获取当前活动仓位")
	@GetMapping(value = "/api/pc/position/current")
	List<CurPositionVo> getCurrentPositionList(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol);
	
}
