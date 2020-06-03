
package com.hp.sh.expv3.fund.cash.action;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.base.web.WebUtils;
import com.hp.sh.chainserver.client.NotifyCreateParams;
import com.hp.sh.chainserver.client.NotifyResultParams;
import com.hp.sh.chainserver.exception.SignException;
import com.hp.sh.expv3.base.BaseApiAction;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.cash.api.ChainCasehApi;
import com.hp.sh.expv3.fund.cash.api.vo.BysCreateResult;
import com.hp.sh.expv3.fund.cash.component.Asset2Symbol;
import com.hp.sh.expv3.fund.cash.component.ExChainService;
import com.hp.sh.expv3.fund.extension.error.ExChainError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * BYS充值回掉接口GET、POST都支持。当前使用
 * @author wangjg
 */
@Api(tags="BYS充值回掉接口GET请求")
@RestController
@RequestMapping("/callback/bys")
public class BysCallbackAction extends BaseApiAction{
	private static final Logger logger = LoggerFactory.getLogger(BysCallbackAction.class);

	@Autowired
	private ChainCasehApi chainCasehApi;
	
	@Autowired
	private ExChainService exChainService;

	@Autowired
	private Asset2Symbol asset2Symbol;
	
	@ApiOperation(value = "创建充值通知")
	@RequestMapping("/deposit/create")
	public BysCreateResult create(HttpServletRequest request){
		Map map = WebUtils.getParameterMap(request);
		map.put("symbol_id", Integer.parseInt(""+map.get("symbol_id")));
		try{
			NotifyCreateParams cp = exChainService.getCreateParams(map);
			String asset = asset2Symbol.getAsset(cp.getSymbolId());
			String sn = chainCasehApi.createDeposit(Long.parseLong(cp.getUserId()), cp.getChainOrderId(), asset, cp.getAddress(), new BigDecimal(cp.getVolume()), cp.getTxHash());
			return new BysCreateResult(sn);
		}catch(SignException se){
			throw new ExException(ExChainError.SIGN_ERROR);
		}
		
	}

	@ApiOperation(value = "支付结果通知")
	@RequestMapping("/deposit/notify/{status}")
	public void depositNotify(@PathVariable String status, HttpServletRequest request){
		Map map = WebUtils.getParameterMap(request);
		map.put("symbol_id", Integer.parseInt(""+map.get("symbol_id")));
		NotifyResultParams np = exChainService.getNotifyParams(map);
		Long userId = Long.parseLong(np.getUserId());
		if("success".equals(status)){
			chainCasehApi.depositSuccess(userId, np.getDepositOrderId());
		}else{
			chainCasehApi.depositFail(userId, np.getDepositOrderId());
		}
	}

}
