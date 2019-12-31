
package com.hp.sh.expv3.fund.cash.action;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.chainserver.client.NotifyCreateParams;
import com.hp.sh.chainserver.client.NotifyResultParams;
import com.hp.sh.chainserver.utils.ChainUtil;
import com.hp.sh.expv3.base.BaseApiAction;
import com.hp.sh.expv3.fund.cash.api.ChainCasehApi;
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
public class TestBysCallbackAction extends BaseApiAction{

	@Autowired
	private ChainCasehApi chainCasehApi;
	
	@Autowired
	private ExChainService exChainService;
	
	@ApiOperation(value = "创建充值通知")
	@PostMapping("/deposit/create")
	public void create(@RequestBody Map map){
		NotifyCreateParams cp = exChainService.getCreateParams(map);
		chainCasehApi.createDeposit(Long.parseLong(cp.getUserId()), cp.getChainOrderId(), cp.getSymbolId(), cp.getAddress(), new BigDecimal(cp.getVolume()), cp.getTxHash());
	}
	
	@ApiOperation(value = "支付结果通知")
	@PostMapping("/deposit/notify/{status}")
	public void depositNotify(@PathVariable String status, @RequestBody Map map){
		NotifyResultParams np = exChainService.getNotifyParams(map);
		int nstatus = "success".equals(status)?1:2;
		Long userId = Long.parseLong(np.getUserId());
		if(nstatus==1){
			chainCasehApi.depositSuccess(userId, np.getDepositOrderId());
		}else if(nstatus==2){
			chainCasehApi.depositFail(userId, np.getDepositOrderId());
		}
	}
	
	@ApiOperation(value = "提现通知")
	@PostMapping("/draw/notify/{status}")
	public void drawNotify(@PathVariable String status, @RequestBody Map map){
		//TODO
		
	}

}
