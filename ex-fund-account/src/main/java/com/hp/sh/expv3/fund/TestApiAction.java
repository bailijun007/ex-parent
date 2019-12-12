
package com.hp.sh.expv3.fund;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.base.BaseApiAction;
import com.hp.sh.expv3.fund.cash.vo.SumAmount;
import com.hp.sh.expv3.pc.api.PcAccountCoreApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 测试接口
 * @author wangjg
 */
@Api(tags="测试接口")
@RestController
@RequestMapping("/test")
public class TestApiAction extends BaseApiAction{

	@Autowired
	private PcAccountCoreApi pcAccountCoreApi;
	
	@ApiOperation(value = "测试接口1")
	@PostMapping("/post1")
	public boolean post1(@RequestBody SumAmount sumAmount){
		//TODO
		return false;
	}

	
	@ApiOperation(value = "测试接口2")
	@PostMapping("/post2")
	public Object post2(@RequestBody Object...params){
		Object b = pcAccountCoreApi.getBalance(1L, "BTC");
		return b;
	}
	
	
	@ApiOperation(value = "测试接口3")
	@PostMapping("/post3")
	public void post3(){
		//TODO
		return;
	}
	
	
	@ApiOperation(value = "测试接口4")
	@PostMapping("/post4")
	public void post4(){
		return;
	}

}
