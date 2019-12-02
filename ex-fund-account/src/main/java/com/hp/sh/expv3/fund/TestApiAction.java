
package com.hp.sh.expv3.fund;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.base.BaseApiAction;
import com.hp.sh.expv3.fund.cash.vo.SumAmount;

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

	
	@ApiOperation(value = "提现通知")
	@PostMapping("/post1")
	public void post1(@RequestBody SumAmount sumAmount){
		//TODO
		return;
	}

	
	@ApiOperation(value = "提现通知")
	@PostMapping("/post2")
	public void post2(@RequestBody Object...params){
		//TODO
		return;
	}
	
	
	@ApiOperation(value = "提现通知")
	@PostMapping("/post3")
	public void post3(){
		//TODO
		return;
	}

}
