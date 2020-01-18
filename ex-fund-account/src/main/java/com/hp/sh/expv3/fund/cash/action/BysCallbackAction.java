
package com.hp.sh.expv3.fund.cash.action;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.gitee.hupadev.commons.json.JsonUtils;
import com.hp.sh.chainserver.client.NotifyCreateParams;
import com.hp.sh.chainserver.client.NotifyResultParams;
import com.hp.sh.expv3.base.BaseApiAction;
import com.hp.sh.expv3.fund.cash.action.request.NotifyCreateBody;
import com.hp.sh.expv3.fund.cash.action.request.NotifyResultBody;
import com.hp.sh.expv3.fund.cash.api.ChainCasehApi;
import com.hp.sh.expv3.fund.cash.api.vo.BysCreateResult;
import com.hp.sh.expv3.fund.cash.component.ExChainService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 充值接口回调接口 demo
 * @author wangjg
 */
@Api(tags="BYS充值回掉接口")
@RestController
@RequestMapping("/callback/bys")
public class BysCallbackAction extends BaseApiAction{
	private static final Logger logger = LoggerFactory.getLogger(BysCallbackAction.class);

	@Autowired
	private ChainCasehApi chainCasehApi;
	
	@Autowired
	private ExChainService exChainService;
	
	@ApiOperation(value = "创建充值通知")
	@PostMapping("/deposit/create")
	public BysCreateResult create(@RequestBody NotifyCreateBody notify){
		logger.info("requestBody:{}", JsonUtils.toJson(notify));
		Map map = BeanHelper.beanToMap(notify);
		NotifyCreateParams cp = exChainService.getCreateParams(map);
		String sn = chainCasehApi.createDeposit(Long.parseLong(cp.getUserId()), cp.getChainOrderId(), cp.getSymbolId(), cp.getAddress(), new BigDecimal(cp.getVolume()), cp.getTxHash());
		return new BysCreateResult(sn);
	}
	
	@ApiOperation(value = "支付结果通知")
	@PostMapping("/deposit/notify/{status}")
	public void depositNotify(@PathVariable String status, @RequestBody NotifyResultBody resultBody){
		Map map = BeanHelper.beanToMap(resultBody);
		NotifyResultParams np = exChainService.getNotifyParams(map);
		Long userId = Long.parseLong(np.getUserId());
		if("success".equals(status)){
			chainCasehApi.depositSuccess(userId, np.getDepositOrderId());
		}else{
			chainCasehApi.depositFail(userId, np.getDepositOrderId());
		}
	}
	
	@ApiOperation(value = "提现通知")
	@PostMapping("/draw/notify/{status}")
	public void drawNotify(@PathVariable String status, @RequestBody Map map){
		//TODO
		
	}

}
